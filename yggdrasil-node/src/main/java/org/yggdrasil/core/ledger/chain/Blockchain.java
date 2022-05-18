package org.yggdrasil.core.ledger.chain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Value;
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
import java.math.BigInteger;
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
    //The software version
    public static final int _VERSION = 0x010;

    // The size of the window in which the hash difficulty is calculated,
    // in the number of blocks
    private final Integer _BLOCK_SOLVE_WINDOW = 2016;
    // The targeted time that blocks should be solved. This target time is
    // set to 14 minutes.
    private final Integer _BLOCK_SOLVE_TIME = 600;
    // The proof of work limit
    private final Integer _POW_LIMIT = 32;
    // The time (in minutes) that blocks recently created and added to the blockchain
    // should expire from the cache.
    @Value("${blockchain.cache.put-expiration:5}")
    private Integer _CACHE_PUT_EXPIRATION;
    // The time (in minutes) that blocks recently retrieved from archive
    // should expire from the cache.
    @Value("${blockchain.cache.get-expiration:5}")
    private Integer _CACHE_GET_EXPIRATION;
    // The base difficulty of the hash computation. This number is dynamic and
    // adjusts automatically to ensure proper solve time.
    private Integer _BASE_DIFFICULTY = 4;
    // This current operating node configuration. Contains identification data
    // about the machine (randomly generated UUIDs) and general node configuration.
    @Autowired
    private transient NodeConfig nodeConfig;
    // The node index is the identifying UUID of this current node. If not defined
    // than a new one is randomly generated.
    private UUID nodeIndex;
    // This is the time since the node was last launched, providing the node a sense
    // of lapse between being turned on/off.
    private ZonedDateTime timestamp;
    // Cache storage for "hot blocks" or blocks that have been used recently or requested
    // by other nodes, in the event they need to be used again. Data is written through the
    // cache to disk, but end up persisting in the cache for a bit longer.
    private transient DB cache;
    // The implementation for the cache.
    private HTreeMap hotBlocks;
    // On-disk storage for the blockchain data. Blocks are written to disk as they are received
    // or generated.
    private transient DB database;
    // The archival, on-disk storage implementation for the blockchain.
    private transient HTreeMap coldBlocks;
    // Storage for the state that the chain was in between runtimes of the node. Primarily
    // just used for storing the last known block so that indexing and sync can occur.
    private transient HTreeMap blockchainState;
    // The last known block's hash. Used for keeping track of the last processed block.
    private transient byte[] lastBlockHash;

    // Initialize the blockchain by defining the databases and restoring previous state.
    @PostConstruct
    public void init() throws Exception {
        // Get the node's name from the configuration
        this.nodeIndex = nodeConfig.getNodeIndex();
        // Get the current timestamp
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
        // If the save directory for the cache is not made create it.
        this.cache = DBMaker
                .memoryDirectDB()
                .make();
        // If the save directory for the archival database is not made create it.
        this.database = DBMaker
                .fileDB(nodeConfig._CURRENT_DIRECTORY + "/chain" + nodeConfig._FILE_EXTENSION)
                .fileMmapEnableIfSupported()
                .make();
        // Create the cold block storage
        this.coldBlocks = this.database
                .hashMap("coldChain")
                .keySerializer(Serializer.BYTE_ARRAY)
                .counterEnable()
                .createOrOpen();
        // Create the blockchain state storage
        this.blockchainState = this.database
                .hashMap("BCState")
                .keySerializer(Serializer.STRING)
                .counterEnable()
                .createOrOpen();
        // Create the hot block storage
        this.hotBlocks = this.cache
                .hashMap("hotChain")
                .keySerializer(Serializer.BYTE_ARRAY)
                .expireAfterCreate(_CACHE_PUT_EXPIRATION, TimeUnit.MINUTES)
                .expireAfterGet(_CACHE_GET_EXPIRATION, TimeUnit.MINUTES)
                .expireOverflow(this.coldBlocks)
                .expireExecutor(Executors.newScheduledThreadPool(2))
                .createOrOpen();
        // If there was state previously stored, restore that state
        this.restoreState();
        // If the cold block storage has nothing in it, generate the genesis block
        // regardless, set the base difficulty, either to the default or calculate
        // if there are blocks that have been stored previously
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

    // Before this object is removed from memory, dump all data to disk.
    @PreDestroy
    public void onDestroy() throws Exception {
        logger.info("Shutting down blockchain database.");
        if (this.lastBlockHash != null) {
            this.blockchainState.put("lastBlockHash", this.lastBlockHash);
        }
        this.hotBlocks.clearWithExpire();
        this.coldBlocks.close();
        this.blockchainState.close();
    }

    // Private function to set the current state from storage
    private void restoreState() {
        this.lastBlockHash = (byte[]) this.blockchainState.get("lastBlockHash");
    }

    /**
     * Get the current node's index (name) as known by the blockchain network.
     *
     * @return nodeIndex
     */
    public UUID getNodeIndex() {
        return nodeIndex;
    }

    /**
     * Return the timestamp since last launch.
     *
     * @return
     */
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Get the current blocks from the cache in an array.
     *
     * @return blocks[]
     */
    public Block[] getBlocks() {
        return (Block[]) this.hotBlocks.values().toArray(Block[]::new);
    }

    /**
     * Add a new block to the chain. This block will be written through to the archive, with a 10m
     * expiration from the cache.
     *
     * @param block
     */
    public void addBlock(Block block) throws Exception {
        logger.trace("Received a block to evaluate for adding to the chain");
        // Previous block
        Block prevBlock = (Block) this.hotBlocks.get(block.getHeader().getPreviousBlockHash());
        // If the block already exists, we throw an exception
        if (((Block) this.hotBlocks.get(block.getBlockHash())) != null) throw new RuntimeException("Duplicate block!");
        // If the previous block as indicated by the incoming block is not in the chain, throw an exception
        if (prevBlock == null) throw new RuntimeException("Previous block not found");
        // If the new block's time is too early, then throw an exception
        if (prevBlock.getHeader().getTime().compareTo(block.getHeader().getTime()) >= 0) throw new RuntimeException("Block's timestamp too early");
        // Check the proof of work

        // Increment the blockHeight
        block.setBlockHeight(prevBlock.getBlockHeight().add(BigInteger.ONE));
        // The block is safe to be placed into the chain storage!
        this.hotBlocks.put(block.getBlockHash(), block);
        // Update the last block hash seen
        this.lastBlockHash = block.getBlockHash();
    }

    /**
     * Add multiple blocks to the chain. These blocks will be written through to the archive, with a 10m
     * expiration from the cache.
     *
     * @param blocks
     * @throws CloneNotSupportedException
     */
    public void addBlocks(List<Block> blocks) throws CloneNotSupportedException {
        for(Block b : blocks) {
            this.hotBlocks.put(b.getBlockHash(), b);
        }
    }

    /**
     * Get a block from disk, indexed by block hash.
     *
     * @param blockHash
     * @return block
     */
    public Optional<Block> getBlock(byte[] blockHash) {
        return Optional.ofNullable((Block) this.hotBlocks.get(blockHash));
    }

    /**
     * Get the last received block from the chain.
     *
     * @return
     */
    @JsonIgnore
    public Optional<Block> getLastBlock() {
        return Optional.ofNullable((Block) this.hotBlocks.get(this.lastBlockHash));
    }

    private boolean compareBlockHash(byte[] frstBlck, byte[] sndBlck) {
        try {
            for (int i = 0; i < frstBlck.length; i++) {
                if (frstBlck[i] != sndBlck[i]) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Check if the chain is valid by comparing the block hashes and if they are connected
     *
     * @param chain
     * @return isValid
     * @throws Exception
     */
    public static boolean isValidChain(HashMap<byte[], Block> chain) throws Exception {
        List<Block> chainBlocks = (List<Block>) chain.values();
        if(!chainBlocks.get(0).toString().contentEquals(Block.genesis().toString())){
            return false;
        }
        for(int i = 1; i < chainBlocks.size(); i++){
            Block b0 = chainBlocks.get(i-1);
            Block b1 = chainBlocks.get(i);
            if(!CryptoHasher.humanReadableHash(b1.getHeader().getPreviousBlockHash()).contentEquals(CryptoHasher.humanReadableHash(b0.getBlockHash())) ||
                    !CryptoHasher.humanReadableHash(b1.getBlockHash()).contentEquals(CryptoHasher.humanReadableHash(CryptoHasher.hash(b1.getHeader())))){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a clone of self.
     *
     * @return blockchain
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Calculate the hashing difficulty based on the solve time between the number
     * of blocks in the block window, with the target time of 14 minutes between blocks.
     *
     * @return
     */
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
            if(lastBlock.getHeader().getPreviousBlockHash() != null) {
                nextLast = this.getBlock(lastBlock.getHeader().getPreviousBlockHash()).get();
                averageTime = averageTime + ((int) (lastBlock.getHeader().getEpochTime() - nextLast.getHeader().getEpochTime()));
            }
            bIndex++;
        }
        if(window != 0) {
            averageTime = averageTime / window;
        }
        if(averageTime > _BLOCK_SOLVE_TIME) {
            this._BASE_DIFFICULTY -= 1;
            return this._BASE_DIFFICULTY;
        } else {
            this._BASE_DIFFICULTY += 1;
            return this._BASE_DIFFICULTY;
        }
    }

}
