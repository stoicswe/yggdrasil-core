package org.yggdrasil.node.network.messages.handlers.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.*;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.InventoryVector;
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionPayload;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;
import org.yggdrasil.node.network.messages.requests.DataMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MempoolTransactionsHandler implements MessageHandler<MempoolTransactionPayload> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Mempool mempool;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private MessagePool messagePool;
    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(MempoolTransactionPayload mempoolTransactionPayload, NodeConnection nodeConnection) throws Exception {

        if(mempoolTransactionPayload.getTransactionsLength() == mempoolTransactionPayload.getTransactions().length) {
            throw new InvalidMessageException("Message received reported wrong data count versus data provided.");
        }

        if(mempoolTransactionPayload.getTransactions().length == 0) {
            throw new InvalidMessageException("Message received did not supply any data.");
        }

        ExpiringMessageRecord exRequest = messagePool.getMessage(mempoolTransactionPayload.getRequestChecksum());

        messagePool.removeMessage(mempoolTransactionPayload.getRequestChecksum());

        Message request = exRequest.getRight();
        DataMessageRequest messageRequest = null;

        switch (request.getCommand()){
            case REQUEST_MEMPOOL_TXNS:
                messageRequest = (DataMessageRequest) request.getPayload();
                break;
            case REQUEST_MEMPOOL_LATEST:
                break;
            default:
                throw new InvalidMessageException("Received an invalid response for the given message request.");
        }

        List<InventoryVector> requestVectors = (messageRequest != null) ? new ArrayList(List.of(messageRequest.getRequestedData())) : null;

        for(TransactionPayload nTxn : mempoolTransactionPayload.getTransactions()) {
            Transaction mTxn = Transaction.Builder.builder()
                    // Build the txn from payload
                    .build();
            if (requestVectors != null) this.removeFound(requestVectors, mTxn.getTxnHash());
            mempool.putTransaction(mTxn);
        }

        if(request.getCommand().isEqual(CommandType.REQUEST_MEMPOOL_TXNS)){
            if(requestVectors.size() > 0) {
                MessagePayload messagePayload = DataMessageRequest.Builder.builder()
                        .setRequestedData(requestVectors.toArray(InventoryVector[]::new))
                        .build();
                Message message = Message.Builder.builder()
                        .setNetwork(nodeConfig.getNetwork())
                        .setRequestType(CommandType.REQUEST_MEMPOOL_TXNS)
                        .setMessagePayload(messagePayload)
                        .setChecksum(CryptoHasher.hash(messagePayload))
                        .build();
                messagePool.putMessage(message, nodeConnection);
                logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
                messenger.sendTargetMessage(message, nodeConnection);
            }
        }
    }

    private void removeFound(List<InventoryVector> vectors, byte[] rTxn) {
        Optional<InventoryVector> v = vectors.stream().filter(i -> CryptoHasher.isEqualHashes(i.getHash(), rTxn)).findFirst();
        v.ifPresent(vectors::remove);
    }
}
