package org.yggdrasil.node.network.messages.handlers.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderResponsePayload;
import org.yggdrasil.node.network.messages.requests.BlockHeaderMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockHeaderRequestHandler implements MessageHandler<BlockHeaderMessageRequest> {

    @Autowired
    private Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(BlockHeaderMessageRequest blockHeaderRequest, NodeConnection nodeConnection) throws Exception {

        MessagePayload messagePayload;
        List<BlockHeaderPayload> headers;

        if(blockHeaderRequest.getHashCount() != blockHeaderRequest.getObjectHashes().length) {
            throw new InvalidMessageException("Message received reported wrong hash count versus data provided.");
        }
        headers = new ArrayList<>();
        for (byte[] blockHash : blockHeaderRequest.getObjectHashes()) {
            Optional<Block> bs = blockchain.getBlock(blockHash);
            if(bs.isPresent()) {
                Block b = bs.get();
                BlockHeaderPayload hp = BlockHeaderPayload.Builder.newBuilder()
                        .setVersion(b.getHeader().getVersion())
                        .setPreviousHash(b.getHeader().getPreviousBlockHash())
                        .setMerkleRoot(b.getHeader().getMerkleRoot())
                        .setTimestamp((int) b.getHeader().getEpochTime())
                        .setDiff(b.getHeader().getDiff())
                        .setNonce(b.getHeader().getNonce())
                        .setTxnCount(b.getTxnCount())
                        .build();
                headers.add(hp);
            }
        }
        messagePayload = BlockHeaderResponsePayload.Builder.newBuilder()
                .setHeaderCount(headers.size())
                .setHeaders(headers.toArray(BlockHeaderPayload[]::new))
                .build();

        return messagePayload;
    }

}
