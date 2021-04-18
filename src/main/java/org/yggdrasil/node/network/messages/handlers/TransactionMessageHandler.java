package org.yggdrasil.node.network.messages.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.transaction.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.TransactionMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;

@Component
public class TransactionMessageHandler implements MessageHandler<TransactionMessage> {

    private Logger logger = LoggerFactory.getLogger(TransactionMessageHandler.class);

    @Autowired
    private Mempool mempool;

    @Override
    public MessagePayload handleMessagePayload(TransactionMessage transactionMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException {
        Transaction txn = Transaction.Builder.newSSTransactionBuilder().buildFromMessage(transactionMessage);
        logger.info("Handling new transaction {}", txn.toString());
        this.mempool.putTransaction(txn);
        return AcknowledgeMessage.Builder.newBuilder()
                .setAcknowledgeChecksum(CryptoHasher.hash(transactionMessage))
                .build();
    }
}
