package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.enums.GetDataType;
import org.yggdrasil.node.network.messages.enums.HeaderType;
import org.yggdrasil.node.network.messages.enums.RequestType;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class GetDataMessageHandler implements MessageHandler<GetDataMessage> {

    @Autowired
    private MessagePool messagePool;
    @Autowired
    private Blockchain blockchain;
    @Autowired
    private Mempool mempool;

    @Override
    public MessagePayload handleMessagePayload(GetDataMessage getDataMessage, NodeConnection nodeConnection) {

        MessagePayload messagePayload = null;

        List<HeaderPayload> headers;
        switch (Objects.requireNonNull(GetDataType.getByValue(getDataMessage.getType()))) {
            case BLOCK:
                headers = new ArrayList<>();
                if(getDataMessage.getHashCount() == 1) {
                    Optional<Block> bs = blockchain.getBlock(getDataMessage.getStopHash());
                    if (bs.isPresent()) {
                        Block b = bs.get();
                        HeaderPayload hp;
                        Object[] txns = null;
                        if(b.getData() instanceof Object[]) {
                            txns = (Object[]) b.getData();
                            for(Object txnObj : txns) {
                                Transaction txn = (Transaction) txnObj;
                                hp = HeaderPayload.Builder.newBuilder()
                                        .setHash(txn.getTxnHash())
                                        .setNonce(txn.getNonce())
                                        .setPreviousHash(new byte[0])
                                        .setTime((int) txn.getTimestamp().toEpochSecond())
                                        .setTransactionCount(-1)
                                        .build();
                                headers.add(hp);
                            }
                        } else {
                            hp = HeaderPayload.Builder.newBuilder()
                                    .setHash(b.getBlockHash())
                                    .setNonce(b.getNonce())
                                    .setPreviousHash((b.getPreviousBlockHash() != null) ? b.getPreviousBlockHash() : new byte[0])
                                    .setTime((int) b.getTimestamp().toEpochSecond())
                                    .setTransactionCount(0)
                                    .build();
                            headers.add(hp);
                        }
                    }
                    messagePayload = HeaderMessage.Builder.newBuilder()
                            .setHeaderType(HeaderType.TXN_HEADER)
                            .setHeaderCount(headers.size())
                            .setHeaders((HeaderPayload[]) headers.toArray())
                            .build();
                } else {
                    throw new InvalidMessageException("Message received reported requested too many items for the type.");
                }
                break;
            case BLOCKCHAIN:
                if(getDataMessage.getHashCount() == getDataMessage.getObjectHashes().length) {
                headers = new ArrayList<>();
                for (byte[] blockHash : getDataMessage.getObjectHashes()) {
                    Optional<Block> bs = blockchain.getBlock(blockHash);
                    if(bs.isPresent()) {
                        Block b = bs.get();
                        HeaderPayload hp = HeaderPayload.Builder.newBuilder()
                                .setTime((int) b.getTimestamp().toEpochSecond())
                                .setHash(b.getBlockHash())
                                .setTransactionCount((b.getData() instanceof Object[]) ? ((Object[]) b.getData()).length : 0)
                                .setPreviousHash(b.getPreviousBlockHash())
                                .build();
                        headers.add(hp);
                    }
                }
                messagePayload = HeaderMessage.Builder.newBuilder()
                        .setHeaderType(HeaderType.TXN_HEADER)
                        .setHeaderCount(headers.size())
                        .setHeaders((HeaderPayload[]) headers.toArray())
                        .build();
                } else {
                    throw new InvalidMessageException("Message received reported wrong hash count versus data provided.");
                }
                break;
            case TRANSACTION:
                if(getDataMessage.getHashCount() == 1) {
                    Optional<Block> bs = blockchain.getBlock(getDataMessage.getStopHash());
                    if(bs.isPresent()){
                        Block b = bs.get();
                        Optional<Transaction> txnObj = b.getTransaction(getDataMessage.getStopHash());
                        if(txnObj.isPresent()) {
                            Transaction txn = txnObj.get();
                            TransactionPayload txnPayload = TransactionPayload.Builder.newBuilder()
                                    .buildFromTransaction(txn)
                                    .setBlockHash(b.getBlockHash())
                                    .build();
                            // make a transaction message
                            messagePayload = TransactionMessage.Builder.newBuilder()
                                    .setTxnCount(1)
                                    .setTxns(new TransactionPayload[]{txnPayload})
                                    .build();
                        }
                    }
                } else {
                    throw new InvalidMessageException("Message received reported requested too many items for the type.");
                }
                break;
            case MEMPOOL:
                // make an array of transaction payloads and put into a
                // transaction message
                if(getDataMessage.getHashCount() > 0) {
                    List<Transaction> transactions = mempool.peekTransaction(getDataMessage.getHashCount());
                    List<TransactionPayload> txnPayloads = new ArrayList<>();
                    for(Transaction txn : transactions) {
                        TransactionPayload txnp = TransactionPayload.Builder.newBuilder()
                                .buildFromTransaction(txn)
                                .setBlockHash(new byte[0])
                                .build();
                        txnPayloads.add(txnp);
                    }
                    messagePayload = TransactionMessage.Builder.newBuilder()
                            .setTxnCount(txnPayloads.size())
                            .setTxns(txnPayloads.toArray(TransactionPayload[]::new))
                            .build();
                } else {
                    throw new InvalidMessageException("Message received reported requested no items for the type.");
                }
                break;
            default:
                break;
        }
        return messagePayload;
    }

}
