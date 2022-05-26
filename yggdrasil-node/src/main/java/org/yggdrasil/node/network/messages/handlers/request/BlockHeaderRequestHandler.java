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
import org.yggdrasil.node.network.messages.payloads.InventoryVector;
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

        List<InventoryVector> missingHashes = new ArrayList<>();
        List<BlockHeaderPayload> headers = new ArrayList<>();

        if(blockHeaderRequest.getHashCount() != blockHeaderRequest.getObjectHashes().length) {
            throw new InvalidMessageException("Message received reported wrong hash count versus data provided.");
        }

        if(blockHeaderRequest.getHashCount() <= 0) {
            throw new InvalidMessageException("Message received requested wrong hash count.");
        }

        Optional<Block> block = null;
        Optional<Block> last = null;

        for (byte[] blockHash : blockHeaderRequest.getObjectHashes()) {
            block = blockchain.getBlock(blockHash);
            if(block.isPresent()) {
                BlockHeaderPayload hp = BlockHeaderPayload.Builder.builder()
                        .setVersion(block.get().getHeader().getVersion())
                        .setPreviousHash(block.get().getHeader().getPreviousBlockHash())
                        .setMerkleRoot(block.get().getHeader().getMerkleRoot())
                        .setTimestamp((int) block.get().getHeader().getEpochTime())
                        .setDiff(block.get().getHeader().getDiff())
                        .setNonce(block.get().getHeader().getNonce())
                        .setTxnCount(block.get().getTxnCount())
                        .build();
                headers.add(hp);
                last = block;
            }
        }
        if(last != null) {
            do {
                block = blockchain.getBlock(block.get().getHeader().getPreviousBlockHash());
                if(block.isPresent()) {
                    if(!block.get().compareBlockHash(blockHeaderRequest.getStopHash())) {
                        headers.add(BlockHeaderPayload.Builder.builder()
                                .setVersion(block.get().getHeader().getVersion())
                                .setPreviousHash(block.get().getHeader().getPreviousBlockHash())
                                .setMerkleRoot(block.get().getHeader().getMerkleRoot())
                                .setTimestamp((int) block.get().getHeader().getEpochTime())
                                .setDiff(block.get().getHeader().getDiff())
                                .setNonce(block.get().getHeader().getNonce())
                                .setTxnCount(block.get().getTxnCount())
                                .build());
                    } else {
                        headers.add(BlockHeaderPayload.Builder.builder()
                                .setVersion(block.get().getHeader().getVersion())
                                .setPreviousHash(block.get().getHeader().getPreviousBlockHash())
                                .setMerkleRoot(block.get().getHeader().getMerkleRoot())
                                .setTimestamp((int) block.get().getHeader().getEpochTime())
                                .setDiff(block.get().getHeader().getDiff())
                                .setNonce(block.get().getHeader().getNonce())
                                .setTxnCount(block.get().getTxnCount())
                                .build());
                        break;
                    }
                } else {
                    break;
                }
            } while (headers.size() < 2500);
        }
        messagePayload = BlockHeaderResponsePayload.Builder.builder()
                .setHeaders(headers.toArray(BlockHeaderPayload[]::new))
                .setRequestChecksum(CryptoHasher.hash(blockHeaderRequest))
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
