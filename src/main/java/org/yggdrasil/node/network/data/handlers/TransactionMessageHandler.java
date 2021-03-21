package org.yggdrasil.node.network.data.handlers;

import org.yggdrasil.core.ledger.transaction.Txn;
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
        if(m.getData() instanceof Txn){
            blockchainService.addNewTransaction((Txn) m.getData());
        }
    }

}
