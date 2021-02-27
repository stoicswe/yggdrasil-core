package org.nathanielbunch.ssblockchain.node.network.data;

import org.nathanielbunch.ssblockchain.core.ledger.Transaction;
import org.nathanielbunch.ssblockchain.node.service.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionMessageHandler implements Runnable {

    @Autowired
    private BlockchainService blockchainService;

    private Message m;

    public TransactionMessageHandler(Message m) {
        this.m = m;
    }

    @Override
    public void run() {
        if(m.getData() instanceof Transaction){
            blockchainService.addNewTransaction((Transaction) m.getData());
        }
    }

}
