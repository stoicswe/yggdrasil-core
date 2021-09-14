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
import org.yggdrasil.node.network.messages.payloads.BlockMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Component
public class BlockMessageHandler implements MessageHandler<BlockMessage>{

    Logger logger = LoggerFactory.getLogger(BlockMessageHandler.class);

    @Autowired
    Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(BlockMessage blockMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

        logger.trace("Handling block message");
        Block blck = Block.Builder.newBuilder().buildFromBlockMessage(blockMessage);
        this.blockchain.addBlock(blck);

        return AcknowledgeMessage.Builder.newBuilder()
                .setAcknowledgeChecksum(CryptoHasher.hash(blockMessage))
                .build();
    }

}
