package org.yggdrasil.node.network.messages.handlers.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.BlockMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

@Component
public class BlockMessageHandler implements MessageHandler<BlockMessage> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Blockchain blockchain;

    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(BlockMessage blockMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        logger.trace("Handling block message");
        Block blck = Block.Builder.builder().buildFromBlockMessage(blockMessage);
        try {
            this.blockchain.addBlock(blck);
        } catch (Exception e) {
            logger.debug("Exception while trying to insert a new block! Exception: {}", e.getMessage());
        }
    }

}
