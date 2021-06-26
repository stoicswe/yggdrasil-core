package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.enums.GetDataType;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class GetDataMessageHandler implements MessageHandler<GetDataMessage> {

    @Autowired
    private Mempool mempool;
    @Autowired
    private MessagePool messagePool;
    @Autowired
    private Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(GetDataMessage getDataMessage, NodeConnection nodeConnection) {

        MessagePayload messagePayload = null;

        List<BlockHeaderPayload> headers;
        switch (Objects.requireNonNull(GetDataType.getByValue(getDataMessage.getType()))) {
            case BLOCK:
                if(getDataMessage.getHashCount() > 0 || getDataMessage.getObjectHashes().length > 0){
                    throw new InvalidMessageException("Message received is invalid for this type of data.");
                }
                Optional<Block> opBlock = blockchain.getBlock(getDataMessage.getStopHash());
                if (opBlock.isPresent()) {
                    Block b = opBlock.get();
                    BlockMessage bm;
                    List<TransactionPayload> txnps = new ArrayList<>();
                    if(b.getData() instanceof List) {
                        List<Transaction> txns = b.getData();
                        for(Object txnObj : txns) {
                            Transaction txn = (Transaction) txnObj;
                            txnps.add(TransactionPayload.Builder.newBuilder()
                                    .setTimestamp((int) txn.getTimestamp().toEpochSecond())
                                    .setDestinationAddress(txn.getDestination())
                                    .setOriginAddress(txn.getOrigin())
                                    .setBlockHash(b.getBlockHash())
                                    .setSignature(txn.getSignature())
                                    .build());
                        }
                        messagePayload = BlockMessage.Builder.newBuilder()
                                .setTimestamp((int) b.getTimestamp().toEpochSecond())
                                .setTxnPayloads(txnps.toArray(TransactionPayload[]::new))
                                .setBlockHash(b.getBlockHash())
                                .setPreviousBlockHash(b.getPreviousBlockHash())
                                .setValidator(b.getValidator())
                                .setSignature(b.getSignature())
                                .build();
                    }
                }
                break;
            case BLOCKCHAIN:
                if(getDataMessage.getHashCount() != getDataMessage.getObjectHashes().length) {
                    throw new InvalidMessageException("Message received reported wrong hash count versus data provided.");
                }
                headers = new ArrayList<>();
                for (byte[] blockHash : getDataMessage.getObjectHashes()) {
                    Optional<Block> bs = blockchain.getBlock(blockHash);
                    if(bs.isPresent()) {
                        Block b = bs.get();
                        BlockHeaderPayload hp = BlockHeaderPayload.Builder.newBuilder()
                                .setTimestamp((int) b.getTimestamp().toEpochSecond())
                                .setHash(b.getBlockHash())
                                .setTransactionCount(b.getData().size())
                                .setPreviousHash(b.getPreviousBlockHash())
                                .build();
                        headers.add(hp);
                    }
                }
                messagePayload = BlockchainMessage.Builder.newBuilder()
                        .setHeaderCount(headers.size())
                        .setHeaders(headers.toArray(BlockHeaderPayload[]::new))
                        .build();
                break;
            case TRANSACTION:
                if(getDataMessage.getHashCount() > 0 || getDataMessage.getObjectHashes().length > 0) {
                    throw new InvalidMessageException("Message received reported requested too many items for the type.");
                }
                Optional<Block> txnOpBlock = blockchain.getBlock(getDataMessage.getStopHash());
                if(txnOpBlock.isPresent()){
                    Block b = txnOpBlock.get();
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
                throw new InvalidMessageException("Message received reported unknown type.");
        }
        return messagePayload;
    }

}
