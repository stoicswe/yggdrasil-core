package org.yggdrasil.node.network.messages.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.BlockTransactions;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;
import org.yggdrasil.node.network.messages.requests.BlockTransactionsRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BlockTxnRequestHandler implements MessageHandler<BlockTransactionsRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Blockchain blockchain;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(BlockTransactionsRequest blockTxnRequest, NodeConnection nodeConnection) throws Exception {

        Message message = null;
        MessagePayload messagePayload = null;

        List<TransactionPayload> blockTxns = new ArrayList<>();

        if(blockTxnRequest.getIndexesCount() != blockTxnRequest.getIndexes().length) {
            throw new InvalidMessageException("Message received reported wrong index count versus data provided.");
        }

        Optional<Block> block = blockchain.getBlock(blockTxnRequest.getHash());
        if(block.isPresent()) {
            List<Transaction> txns = block.get().getData();

            if(blockTxnRequest.getIndexesCount() > txns.size()) {
                throw new InvalidMessageException("Requested too many txns for this block.");
            }

            if(blockTxnRequest.getIndexesCount() == 0) {
                for(Transaction txn : txns) {
                    blockTxns.add(TransactionPayload.Builder.builder()
                            .setVersion(Blockchain._VERSION)
                            // TODO: Implement the txn properly
                            //        .setWitnessFlag()
                            //        .setTxIns()
                            //        .setTxOuts()
                            //        .setWitnesses()
                            //       .setLockTime()
                            .build());
                }
            } else {
                for (int index : blockTxnRequest.getIndexes()) {
                    Transaction txn = txns.get(index);
                    blockTxns.add(TransactionPayload.Builder.builder()
                            .setVersion(Blockchain._VERSION)
                            // TODO: Implement the txn properly
                            //        .setWitnessFlag()
                            //        .setTxIns()
                            //        .setTxOuts()
                            //        .setWitnesses()
                            //       .setLockTime()
                            .build());
                }
            }
            messagePayload = BlockTransactions.Builder.builder()
                    .setBlockHash(block.get().getBlockHash())
                    .setTransactions(blockTxns.toArray(TransactionPayload[]::new))
                    .setRequestChecksum(CryptoHasher.hash(blockTxnRequest))
                    .build();
            message = Message.Builder.builder()
                    .setNetwork(nodeConfig.getNetwork())
                    .setRequestType(CommandType.BLOCK_TXN_PAYLOAD)
                    .setMessagePayload(messagePayload)
                    .setChecksum(CryptoHasher.hash(messagePayload))
                    .build();
            logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
            messenger.sendTargetMessage(message, nodeConnection);
        } else {
            throw new InvalidMessageException("Block was not present in local chain.");
        }
    }

}
