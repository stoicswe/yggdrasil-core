package org.yggdrasil.node.network.messages.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionMessage;
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class MempoolTransactionMessageHandler implements MessageHandler<MempoolTransactionMessage> {

    private final Logger logger = LoggerFactory.getLogger(MempoolTransactionMessageHandler.class);

    @Autowired
    private Mempool mempool;

    @Override
    public MessagePayload handleMessagePayload(MempoolTransactionMessage basicTransactionMessage, NodeConnection nodeConnection) throws Exception {
        if (basicTransactionMessage.getTxns().length == basicTransactionMessage.getTxnCount()) {
            for (MempoolTransactionPayload txnp : basicTransactionMessage.getTxns()) {
                Transaction txn = Transaction.Builder.builder().buildFromMessage(txnp);
                logger.info("Handling new basic transaction {}", txn.toString());
                this.mempool.putTransaction(txn);
            }
        }

        return AcknowledgeMessage.Builder.newBuilder()
                .setAcknowledgeChecksum(CryptoHasher.hash(basicTransactionMessage))
                .build();
    }
}
