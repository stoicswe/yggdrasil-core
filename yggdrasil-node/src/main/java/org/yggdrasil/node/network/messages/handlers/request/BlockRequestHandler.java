package org.yggdrasil.node.network.messages.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.enums.GetDataType;
import org.yggdrasil.node.network.messages.enums.InventoryType;
import org.yggdrasil.node.network.messages.enums.RejectCodeType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.messages.requests.BlockMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class BlockRequestHandler implements MessageHandler<BlockMessageRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Blockchain blockchain;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(BlockMessageRequest blockMessageRequest, NodeConnection nodeConnection) throws NoSuchAlgorithmException, IOException {

        Message message = null;
        MessagePayload messagePayload = null;

        List<InventoryVector> missingHashes = new ArrayList<>();
        List<InventoryVector> invs = new ArrayList<>();

        if(blockMessageRequest.getHashCount() == blockMessageRequest.getObjectHashes().length) {
            if(blockMessageRequest.getHashCount() > 0) {
                Optional<Block> block = null;
                int blockMessageRequestIndex = 0;
                for(int i = 0; i < blockMessageRequest.getHashCount(); i++) {
                    byte[] reqHash = blockMessageRequest.getObjectHashes()[i];
                    block = blockchain.getBlock(reqHash);
                    if(block.isPresent()) {
                        blockMessageRequestIndex = i;
                        break;
                    } else {
                        missingHashes.add(InventoryVector.Builder.builder().setType(InventoryType.MSG_BLOCK).setHash(reqHash).build());
                    }
                }

                if(block.isPresent()) {
                    do {
                        if(block.isPresent()) {
                            InventoryVector v = InventoryVector.Builder.builder()
                                    .setType(InventoryType.MSG_BLOCK)
                                    .setHash(block.get().getBlockHash())
                                    .build();
                            invs.add(v);
                            missingHashes.remove(block.get().getBlockHash());
                            block = blockchain.getBlock(block.get().getHeader().getPreviousBlockHash());
                        } else {
                            if(blockMessageRequestIndex < blockMessageRequest.getHashCount()){
                                blockMessageRequestIndex++;
                            }
                            block = blockchain.getBlock(blockMessageRequest.getObjectHashes()[blockMessageRequestIndex]);
                            if(block.isEmpty()) missingHashes.add(InventoryVector.Builder.builder().setType(InventoryType.MSG_BLOCK).setHash(blockMessageRequest.getObjectHashes()[blockMessageRequestIndex]).build());
                        }
                    } while(invs.size() < 500);

                    messagePayload = InventoryMessage.Builder.builder()
                            .setInventory(invs.toArray(InventoryVector[]::new))
                            .build();
                    message = Message.Builder.builder()
                            .setNetwork(nodeConfig.getNetwork())
                            .setRequestType(CommandType.INVENTORY_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
                    messenger.sendTargetMessage(message, nodeConnection);
                }

                if(missingHashes.size() > 0) {
                    messagePayload = NotFoundResponsePayload.Builder.builder()
                            .setMissingItems(missingHashes.toArray(InventoryVector[]::new))
                            .build();
                    message = Message.Builder.builder()
                            .setNetwork(nodeConfig.getNetwork())
                            .setRequestType(CommandType.INVENTORY_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
                    messenger.sendTargetMessage(message, nodeConnection);
                }
            } else {
                // return error msg
                messagePayload = RejectMessagePayload.Builder.builder()
                        .setRejectCode(RejectCodeType.REJECT_INVALID)
                        .setMessage("Requested block messages was empty.")
                        .setData(CryptoHasher.hash(blockMessageRequest))
                        .build();
                message = Message.Builder.builder()
                        .setNetwork(nodeConfig.getNetwork())
                        .setRequestType(CommandType.REJECT_PAYLOAD)
                        .setMessagePayload(messagePayload)
                        .setChecksum(CryptoHasher.hash(messagePayload))
                        .build();
                logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
                messenger.sendTargetMessage(message, nodeConnection);
            }
        } else {
            // return error msg
            messagePayload = RejectMessagePayload.Builder.builder()
                    .setRejectCode(RejectCodeType.REJECT_INVALID)
                    .setMessage("Lied about number of items.")
                    .setData(CryptoHasher.hash(blockMessageRequest))
                    .build();
            message = Message.Builder.builder()
                    .setNetwork(nodeConfig.getNetwork())
                    .setRequestType(CommandType.REJECT_PAYLOAD)
                    .setMessagePayload(messagePayload)
                    .setChecksum(CryptoHasher.hash(messagePayload))
                    .build();
            logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
            messenger.sendTargetMessage(message, nodeConnection);
        }


    }

}
