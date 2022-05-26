package org.yggdrasil.node.network.messages.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderResponsePayload;
import org.yggdrasil.node.network.messages.requests.BlockHeaderMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BlockHeaderRequestHandler implements MessageHandler<BlockHeaderMessageRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Blockchain blockchain;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(BlockHeaderMessageRequest blockHeaderRequest, NodeConnection nodeConnection) throws Exception {

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
        messagePayload = BlockHeaderResponsePayload.Builder.builder()
                .setHeaders(headers.toArray(BlockHeaderPayload[]::new))
                .build();
        Message message = Message.Builder.builder()
                .setNetwork(nodeConfig.getNetwork())
                .setRequestType(CommandType.BLOCK_HEADER_PAYLOAD)
                .setMessagePayload(messagePayload)
                .setChecksum(CryptoHasher.hash(messagePayload))
                .build();
        logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
        messenger.sendTargetMessage(message, nodeConnection);
    }

}
