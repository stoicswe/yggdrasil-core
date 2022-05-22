package org.yggdrasil.node.network.messages.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderResponsePayload;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlockchainMessageHandler implements MessageHandler<BlockHeaderResponsePayload> {

    Logger logger = LoggerFactory.getLogger(BlockchainMessageHandler.class);

    @Autowired
    Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(BlockHeaderResponsePayload blockHeaderResponsePayload, NodeConnection nodeConnection) throws NoSuchAlgorithmException {

        logger.trace("Handling blockchain message");
        if(blockHeaderResponsePayload.getHeaders().length == blockHeaderResponsePayload.getHeaderCount()) {
            List<Block> blcks = new ArrayList<>();
            for(BlockHeaderPayload hp : blockHeaderResponsePayload.getHeaders()) {
                Block blck = Block.Builder.newBuilder().buildFromBlockHeaderMessage(hp);
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
        }

        return AcknowledgeMessage.Builder.newBuilder()
                .setAcknowledgeChecksum(CryptoHasher.hash(blockHeaderResponsePayload))
                .build();

    }
}
