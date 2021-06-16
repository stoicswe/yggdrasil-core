package org.yggdrasil.core.ledger.chain;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.BlockchainIO;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.NodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private transient NodeConfig nodeConfig;
    @Autowired
    private transient BlockchainIO blockchainIO;
    // This hotBlock functionality is disabled for now
    // Future impl will include a moving window technique
    private transient final Integer hotblocks = -1;
    private transient final Object lock = new Object();

    private UUID nodeIndex;
    private ZonedDateTime timestamp;
    private HashMap<byte[], Block> blocks;

    @PostConstruct
    public void init() throws Exception {
        this.nodeIndex = nodeConfig.getNodeIndex();
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
        this.blocks = new HashMap<>();
        Block genBlock = Block.genesis();
        this.blocks.put(genBlock.getBlockHash(), genBlock);
    }

    public UUID getNodeIndex() {
        return nodeIndex;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public Block[] getBlocks() {
        return blocks.values().toArray(Block[]::new);
    }

    public void addBlock(Block block) {
        logger.trace("Received a block to evaluate for adding to the chain");
        synchronized (lock) {
            Block storedBlck = this.blocks.get(block.getBlockHash());
            if (storedBlck != null) {
                if (storedBlck.getTimestamp().compareTo(block.getTimestamp()) > 0) {
                    this.blocks.replace(block.getBlockHash(), block);
                } else if (storedBlck.getTimestamp().compareTo(block.getTimestamp()) == 0) {
                    if (storedBlck.getData().size() < block.getData().size()) {
                        this.blocks.replace(block.getBlockHash(), block);
                    }
                }
            } else {
                this.blocks.put(block.getBlockHash(), block);
            }
        }
    }

    public void addBlocks(List<Block> blocks) throws CloneNotSupportedException {
        synchronized (lock) {
            for(Block b : blocks) {
                this.blocks.put(b.getBlockHash(), b);
            }
        }
        this.checkBlocks();
    }

    public Optional<Block> getBlock(byte[] blockHash) {
        return Optional.ofNullable(this.blocks.get(blockHash));
    }

    public void checkBlocks() throws CloneNotSupportedException {
        // If the blockchain dump is disabled, then skip sync lock
        if(hotblocks != -1) {
            synchronized (lock) {
                if (this.blocks.size() > hotblocks) {
                    try {
                        blockchainIO.dumpChain((Blockchain) this.clone());
                        this.blocks = new HashMap<>();
                    } catch (IOException ie) {
                        logger.error("Cannot dump blocks to storage.");
                    }
                }
            }
        }
    }

    public void replaceChain(HashMap<byte[], Block> newChain) throws Exception {

        if(newChain.size() <= this.blocks.size()) {
            logger.debug("Received chain of length [{}] is not longer than local chain size [{}].", newChain.size(), this.blocks.size());
            return;
        } else if(!isValidChain(newChain)) {
            logger.warn("Received chain is invalid.");
            return;
        }

        logger.info("Replacing local chain with incoming chain.");
        this.blocks = newChain;

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

}
