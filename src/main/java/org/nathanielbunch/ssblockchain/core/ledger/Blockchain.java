package org.nathanielbunch.ssblockchain.core.ledger;

import org.nathanielbunch.ssblockchain.core.utils.BlockchainIO;
import org.nathanielbunch.ssblockchain.core.utils.DateTimeUtil;
import org.nathanielbunch.ssblockchain.node.network.NodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class Blockchain implements Cloneable {

    private Logger logger = LoggerFactory.getLogger(Blockchain.class);

    @Autowired
    private transient NodeConfig nodeConfig;
    @Autowired
    private transient BlockchainIO blockchainIO;
    @Value("${blockchain.hot-blocks}")
    private transient Integer hotblocks;
    private transient final Object lock = new Object();

    private UUID nodeIndex;
    private ZonedDateTime timestamp;
    private List<Block> blocks;

    @PostConstruct
    public void init() {
        this.nodeIndex = nodeConfig.getNodeIndex();
        this.timestamp = DateTimeUtil.getCurrentTimestamp();
        this.blocks = new ArrayList<>();
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
