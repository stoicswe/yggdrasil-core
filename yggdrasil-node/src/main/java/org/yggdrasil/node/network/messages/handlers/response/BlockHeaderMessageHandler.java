package org.yggdrasil.node.network.messages.handlers.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderResponsePayload;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.messages.requests.BlockTransactionsRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlockHeaderMessageHandler implements MessageHandler<BlockHeaderResponsePayload> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Blockchain blockchain;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(BlockHeaderResponsePayload blockHeaderResponsePayload, NodeConnection nodeConnection) throws NoSuchAlgorithmException, IOException {

        logger.trace("Handling blockchain message");
        if(blockHeaderResponsePayload.getHeaders().length == blockHeaderResponsePayload.getHeaderCount()) {
            List<Block> blcks = new ArrayList<>();
            for(BlockHeaderPayload hp : blockHeaderResponsePayload.getHeaders()) {
                Block blck = Block.Builder.builder().buildFromBlockHeaderMessage(hp);
                logger.debug("Rebuilt block {} from block header payload.", blck.toString());
                if(blockchain.getBlock(blck.getBlockHash()).isEmpty()) {
                    logger.debug("Block is not present in local chain, adding block");
                    blcks.add(blck);
                }
            }
            logger.debug("New blocks to be added to local blockchain: {}", blcks.size());
            try {
                blockchain.addBlocks(blcks);
            } catch (CloneNotSupportedException e) {
                // do nothing
            }
            Message m;
            MessagePayload messagePayload;
            for(Block b : blcks) {
                messagePayload = BlockTransactionsRequest.Builder.builder()
                        .setHeader(CryptoHasher.hash(b.getHeader()))
                        .setIndexes(new int[0])
                        .build();
                m = Message.Builder.builder()
                        .setNetwork(nodeConfig.getNetwork())
                        .setRequestType(CommandType.REQUEST_BLOCK_TXNS)
                        .setMessagePayload(messagePayload)
                        .setChecksum(CryptoHasher.hash(messagePayload))
                        .build();
                messenger.sendTargetMessage(m, nodeConnection);
            }
        }
    }
}
