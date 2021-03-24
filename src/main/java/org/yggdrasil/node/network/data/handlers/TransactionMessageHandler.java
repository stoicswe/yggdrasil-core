package org.yggdrasil.node.network.data.handlers;

import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.node.network.data.Message;
import org.yggdrasil.node.service.BlockchainService;
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
