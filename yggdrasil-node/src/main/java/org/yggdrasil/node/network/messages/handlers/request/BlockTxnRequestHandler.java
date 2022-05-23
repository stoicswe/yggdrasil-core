package org.yggdrasil.node.network.messages.handlers.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.BlockTransactions;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;
import org.yggdrasil.node.network.messages.requests.BlockTransactionsRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockTxnRequestHandler implements MessageHandler<BlockTransactionsRequest> {

    @Autowired
    private Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(BlockTransactionsRequest blockTxnRequest, NodeConnection nodeConnection) throws Exception {

        MessagePayload messagePayload = null;

        BlockTransactions blockTransactions;
        List<TransactionPayload> blockTxns = new ArrayList<>();
        Optional<Block> block = blockchain.getBlock(blockTxnRequest.getHash());
        if(block.isPresent()) {
            for(Transaction txn : block.get().getData()) {
                blockTxns.add(TransactionPayload.Builder.builder()
                                .setVersion(Blockchain._VERSION)
                                .setWitnessFlag()
                                .setTxIns()
                                .setTxOuts()
                                .setWitnesses()
                                .setLockTime()
                        .build());
            }
            blockTransactions = BlockTransactions.Builder.builder()
                    .setBlockHash(block.get().getBlockHash())
                    .setTransactions(blockTxns.toArray(TransactionPayload[]::new))
                    .build();
            messagePayload = blockTransactions;
        } else {
            // Build the error payload to send back
        }

        return messagePayload;
    }

}
