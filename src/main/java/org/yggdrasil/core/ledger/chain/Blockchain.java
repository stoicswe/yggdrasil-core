package org.yggdrasil.core.ledger.chain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.NodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is the class definition for the blockchain object. Its purpose
 * is for storing transaction records.
 *
 * @since 0.0.7
 * @author nathanielbunch
 */
@Component
@JsonInclude
public class Blockchain implements Cloneable {

    private Logger logger = LoggerFactory.getLogger(Blockchain.class);
    // The size of the window in which the hash difficulty is calculated
    private final Integer _BLOCK_SOLVE_WINDOW = 2016;
    private Integer _BASE_DIFFICULTY = 4;

    @Autowired
    private transient NodeConfig nodeConfig;
    // Node name reference
    private UUID nodeIndex;
    // Time since last node launch
    private ZonedDateTime timestamp;
    // Data storage
    private transient DB cache;
    private HTreeMap hotBlocks;
    private transient DB database;
    private transient HTreeMap coldBlocks;
    private transient HTreeMap blockchainState;
    private transient byte[] lastBlockHash;

    @PostConstruct
    public void init() throws Exception {
        this.nodeIndex = nodeConfig.getNodeIndex();
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
        // If the save directory is not made, make it
        this.cache = DBMaker
                .memoryDirectDB()
                .make();
        this.database = DBMaker
                .fileDB(nodeConfig._CURRENT_DIRECTORY + "/chain" + nodeConfig._FILE_EXTENSION)
                .fileMmapEnableIfSupported()
                .make();
        this.coldBlocks = this.database
                .hashMap("coldChain")
                .keySerializer(Serializer.BYTE_ARRAY)
                .counterEnable()
                .createOrOpen();
        this.blockchainState = this.database
                .hashMap("BCState")
                .keySerializer(Serializer.STRING)
                .counterEnable()
                .createOrOpen();
        this.hotBlocks = this.cache
                .hashMap("hotChain")
                .keySerializer(Serializer.BYTE_ARRAY)
                .expireAfterCreate(30, TimeUnit.MINUTES)
                .expireAfterGet(15, TimeUnit.MINUTES)
                .expireOverflow(this.coldBlocks)
                .expireExecutor(Executors.newScheduledThreadPool(2))
                .createOrOpen();
        // restore necessary values
        this.restoreState();
        if(this.coldBlocks.size() == 0) {
            Block genesis = Block.genesis();
            this.addBlock(genesis);
            this._BASE_DIFFICULTY = 4;
        } else if(this.coldBlocks.size() == 1){
            this._BASE_DIFFICULTY = 4;
        } else {
            this._BASE_DIFFICULTY = this.calculateDifficulty();
        }
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        logger.info("Shutting down blockchain database.");
        this.hotBlocks.clearWithExpire();
        this.coldBlocks.close();
        this.blockchainState.close();
    }

    private void restoreState() {
        this.lastBlockHash = (byte[]) this.blockchainState.get("lastBlockHash");
    }

    public UUID getNodeIndex() {
        return nodeIndex;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public Block[] getBlocks() {
        return (Block[]) this.hotBlocks.values().toArray(Block[]::new);
    }

    public void addBlock(Block block) {
        logger.trace("Received a block to evaluate for adding to the chain");
        Block storedBlck = (Block) this.hotBlocks.get(block.getBlockHash());
        if (storedBlck != null) {
            if (storedBlck.getTimestamp().compareTo(block.getTimestamp()) > 0) {
                this.hotBlocks.replace(block.getBlockHash(), block);
            } else if (storedBlck.getTimestamp().compareTo(block.getTimestamp()) == 0) {
                if (storedBlck.getData().size() < block.getData().size()) {
                    this.hotBlocks.replace(block.getBlockHash(), block);
                }
            }
        } else {
            this.hotBlocks.put(block.getBlockHash(), block);
            this.lastBlockHash = block.getBlockHash();
            this.blockchainState.put("lastBlockHash", this.lastBlockHash);
        }
    }

    public void addBlocks(List<Block> blocks) throws CloneNotSupportedException {
        for(Block b : blocks) {
            this.hotBlocks.put(b.getBlockHash(), b);
        }
    }

    public Optional<Block> getBlock(byte[] blockHash) {
        return Optional.ofNullable((Block) this.hotBlocks.get(blockHash));
    }

    @JsonIgnore
    public Optional<Block> getLastBlock() {
        return Optional.ofNullable((Block) this.hotBlocks.get(this.lastBlockHash));
    }

    public static boolean isValidChain(HashMap<byte[], Block> chain) throws Exception {
        List<Block> chainBlocks = (List<Block>) chain.values();
        if(!chainBlocks.get(0).toString().contentEquals(Block.genesis().toString())){
            return false;
        }
        for(int i = 1; i < chainBlocks.size(); i++){
            Block b0 = chainBlocks.get(i-1);
            Block b1 = chainBlocks.get(i);
            if(!CryptoHasher.humanReadableHash(b1.getPreviousBlockHash()).contentEquals(CryptoHasher.humanReadableHash(b0.getBlockHash())) ||
                    !CryptoHasher.humanReadableHash(b1.getBlockHash()).contentEquals(CryptoHasher.humanReadableHash(CryptoHasher.hash(b1)))){
                return false;
            }
        }
        return true;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected int calculateDifficulty() {
        int window = this._BLOCK_SOLVE_WINDOW;
        if(this.coldBlocks.size() < this._BLOCK_SOLVE_WINDOW) {
            window = this.coldBlocks.size();
        }
        Block lastBlock = this.getBlock(this.lastBlockHash).get();
        int averageTime = 1;
        int bIndex = 0;
        while(bIndex <= window) {
            Block nextLast = null;
            if(lastBlock.getPreviousBlockHash() != null) {
                nextLast = this.getBlock(lastBlock.getPreviousBlockHash()).get();
                averageTime = averageTime + ((int) (lastBlock.getTimestamp().toEpochSecond() - nextLast.getTimestamp().toEpochSecond()));
            }
            bIndex++;
        }
        if(window != 0) {
            averageTime = averageTime / window;
        }
        if(averageTime < 600) {
            this._BASE_DIFFICULTY -= 1;
            return this._BASE_DIFFICULTY;
        } else {
            this._BASE_DIFFICULTY += 1;
            return this._BASE_DIFFICULTY;
        }
    }

}
