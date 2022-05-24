package org.yggdrasil.node.network.messages.handlers.request;

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
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.messages.requests.BlockMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BlockRequestHandler implements MessageHandler<BlockMessageRequest> {

    @Autowired
    private Mempool mempool;
    @Autowired
    private MessagePool messagePool;
    @Autowired
    private Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(BlockMessageRequest blockMessageRequest, NodeConnection nodeConnection) throws NoSuchAlgorithmException {

        MessagePayload messagePayload = null;

        List<BlockHeaderPayload> headers;
        switch (Objects.requireNonNull(blockMessageRequest.getType())) {

            /*
            case BLOCK:
                if(blockMessageRequest.getHashCount() > 0 || blockMessageRequest.getObjectHashes().length > 0){
                    throw new InvalidMessageException("Message received is invalid for this type of data.");
                }
                Optional<Block> opBlock = blockchain.getBlock(blockMessageRequest.getStopHash());
                if (opBlock.isPresent()) {
                    Block b = opBlock.get();
                    BlockMessage bm;
                    List<TransactionPayload> txnps = new ArrayList<>();
                    if(b.getData() instanceof List) {
                        List<Transaction> txns = b.getData();
                        for(Object txnObj : txns) {
                            Transaction txn = (Transaction) txnObj;
                            txnps.add(TransactionPayload.Builder.builder()
                                            .setVersion(Blockchain._VERSION)
                                    // TODO: Fix setting TXNs
                                            .setWitnessFlag(false)
                                            .setTxIns(new TransactionIn[0])
                                            .setTxOuts(new TransactionOut[0])
                                            .setWitnesses(new TransactionWitness[0])
                                            .setLockTime(0)
                                            .build());
                        }
                        messagePayload = BlockMessage.Builder.builder()
                                .setVersion(b.getHeader().getVersion())
                                .setPreviousBlock(b.getHeader().getPreviousBlockHash())
                                .setMerkleRoot(b.getHeader().getMerkleRoot())
                                .setTimestamp((int) b.getHeader().getEpochTime())
                                .setDiff(b.getHeader().getDiff())
                                .setNonce(b.getHeader().getNonce())
                                .setTxnCount(txnps.size())
                                .setTxnPayloads(txnps.toArray(TransactionPayload[]::new))
                                .build();
                    }
                }
                break;
            case TRANSACTION:

            case MEMPOOL:
                // make an array of transaction payloads and put into a
                // transaction message
                if(blockMessageRequest.getHashCount() > 0) {
                    List<Transaction> transactions = mempool.peekTransaction(blockMessageRequest.getHashCount());
                    List<MempoolTransactionPayload> txnPayloads = new ArrayList<>();
                    for(Transaction txn : transactions) {
                        MempoolTransactionPayload txnp = MempoolTransactionPayload.Builder.builder()
                                .buildFromMempool(txn);
                        txnPayloads.add(txnp);
                    }
                    messagePayload = MempoolTransactionMessage.Builder.builder()
                            .setTxnCount(txnPayloads.size())
                            .setTxns(txnPayloads.toArray(MempoolTransactionPayload[]::new))
                            .build();
                } else {
                    throw new InvalidMessageException("Message received reported requested no items for the type.");
                }
                break;
            default:
                throw new InvalidMessageException("Message received reported unknown type.");
             */
        }
        return messagePayload;
    }

}
