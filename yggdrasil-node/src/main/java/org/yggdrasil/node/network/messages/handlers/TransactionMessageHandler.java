package org.yggdrasil.node.network.messages.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TransactionMessageHandler implements MessageHandler<TransactionMessage> {

    private Logger logger = LoggerFactory.getLogger(TransactionMessageHandler.class);

    @Autowired
    private Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(TransactionMessage transactionMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        // need to add some witness validation for the
        // incoming txns to make sure duplicates are not
        // added to the mempool. Witnesses *could* be in the
        // message headers and hashes of the IP addresses.
        if(transactionMessage.getTxns().length == transactionMessage.getTxnCount()) {
            for(TransactionPayload txnp : transactionMessage.getTxns()) {
                Transaction txn = Transaction.Builder.builder().buildFromMessage(txnp);
                logger.info("Handling new transaction {}", txn.toString());
                if(txnp.getBlockHash() != null && txnp.getBlockHash().length > 0){
                    Optional<Block> blck =  blockchain.getBlock(txnp.getBlockHash());
                    if(blck.isPresent()) {
                        if(blck.get().getData() instanceof ArrayList) {
                            List<Transaction> txns = (ArrayList<Transaction>) blck.get().getData();
                            txns.add(txn);
                        }
                    } else {
                        throw new InvalidMessageException("Transaction message had a non-existent block hash.");
                    }
                } else {
                    throw new InvalidMessageException("Transaction message had some missing values.");
                }
            }
        }

        return AcknowledgeMessage.Builder.builder()
                .setAcknowledgeChecksum(CryptoHasher.hash(transactionMessage))
                .build();

    }
}
