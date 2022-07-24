package org.yggdrasil.node.network.messages.handlers.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.BlockHeader;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.ExpiringMessageRecord;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.BlockTransactions;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BlockTransactionsHandler implements MessageHandler<BlockTransactions> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Blockchain blockchain;

    @Autowired
    private MessagePool messagePool;

    @Override
    public void handleMessagePayload(BlockTransactions blockTransactions, NodeConnection nodeConnection) throws Exception {

        if(blockTransactions.getTransactionsLength() != blockTransactions.getTransactions().length) throw new InvalidMessageException("Transaction count did not match with txn array size.");

        if(blockTransactions.getTransactions().length == 0) throw new InvalidMessageException("Block transactions received was empty.");

        ExpiringMessageRecord exMessage = messagePool.getMessage(blockTransactions.getRequestChecksum());
        if(exMessage == null) throw new InvalidMessageException("Block transactions message was sent to a non existent request.");
        Message request = exMessage.getRight();
        if(!request.getCommand().isEqual(CommandType.REQUEST_BLOCK_TXNS)) throw new InvalidMessageException("Block txns was not sent in response to a REQUEST_BLOCK_TXNS msg.");

        Optional<Block> block = blockchain.getBlock(blockTransactions.getBlockHash());

        if(block.isEmpty()) throw new InvalidMessageException("Block transactions payload references a non existent block.");

        if(block.get().getData().size() > 0) throw new InvalidMessageException("Block transactions can not be overwritten.");

        List<Transaction> bTxns = new ArrayList<>();

        for(TransactionPayload pTxn : blockTransactions.getTransactions()) {
            bTxns.add(Transaction.Builder.builder()
                            // Build the txn;
                    .build());
        }

        byte[] merkleRoot = CryptoHasher.generateMerkleTree(bTxns);

        if(!CryptoHasher.isEqualHashes(merkleRoot, block.get().getHeader().getMerkleRoot())) throw new InvalidMessageException("Merkle root for block header and incoming txn data does not match");

        BlockHeader header = BlockHeader.Builder.builder()
                .setVersion(block.get().getHeader().getVersion())
                .setPreviousBlockHash(block.get().getHeader().getPreviousBlockHash())
                .setMerkleRoot(merkleRoot)
                .setTime(block.get().getHeader().getTime())
                .setDiff(block.get().getHeader().getDiff())
                .setNonce(block.get().getHeader().getNonce())
                .build();

        Block newBlock = Block.Builder.builder()
                .setBlockHeader(header)
                .setData(bTxns)
                .setBlockHeight(block.get().getBlockHeight())
                .build();

        blockchain.addBlock(newBlock);

        messagePool.removeMessage(request.getChecksum());
    }

}
