package org.nathanielbunch.ssblockchain.node.network.data;

import org.nathanielbunch.ssblockchain.core.ledger.Block;
import org.nathanielbunch.ssblockchain.core.ledger.Blockchain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BlockMessageHandler implements Runnable {

    Logger logger = LoggerFactory.getLogger(BlockMessageHandler.class);

    @Autowired
    private Blockchain blockchain;

    private Message m;

    public BlockMessageHandler(Message m) {
        this.m = m;
    }

    @Override
    public void run() {
        if(m.getData() instanceof Block) {
            try {
                this.blockchain.addBlocks(List.of((Block) m.getData()));
            } catch (CloneNotSupportedException e) {
                logger.warn("Block not inserted into local chain: {}", e.getLocalizedMessage());
            }
        }
    }
}
