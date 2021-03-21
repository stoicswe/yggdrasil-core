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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This is the class definition for the blockchain object. Its purpose
 * is for storing transaction records.
 *
 * @since 0.0.1
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
    private List<Block> blocks;

    @PostConstruct
    public void init() throws Exception {
        this.nodeIndex = nodeConfig.getNodeIndex();
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
        this.blocks = new ArrayList<>();
        this.blocks.add(Block.genesis());
    }

    public UUID getNodeIndex() {
        return nodeIndex;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public Block[] getBlocks() {
        return blocks.toArray(Block[]::new);
    }

    public void addBlocks(List<Block> blocks) throws CloneNotSupportedException {
        synchronized (lock) {
            this.blocks.addAll(blocks);
        }
        this.checkBlocks();
    }

    public void checkBlocks() throws CloneNotSupportedException {
        // If the blockchain dump is disabled, then skip sync lock
        if(hotblocks != -1) {
            synchronized (lock) {
                if (this.blocks.size() > hotblocks) {
                    try {
                        blockchainIO.dumpChain((Blockchain) this.clone());
                        this.blocks = new ArrayList<>();
                    } catch (IOException ie) {
                        logger.error("Cannot dump blocks to storage.");
                    }
                }
            }
        }
    }

    public void replaceChain(List<Block> newChain) throws Exception {

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

    public static boolean isValidChain(List<Block> chain) throws Exception {
        if(!chain.get(0).toString().contentEquals(Block.genesis().toString())){
            return false;
        }
        for(int i = 1; i < chain.size(); i++){
            Block b0 = chain.get(i-1);
            Block b1 = chain.get(i);
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
