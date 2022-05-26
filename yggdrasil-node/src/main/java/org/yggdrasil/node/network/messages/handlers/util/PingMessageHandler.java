package org.yggdrasil.node.network.messages.handlers.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Component
public class PingMessageHandler implements MessageHandler<PingPongMessage> {
    private static final Logger logger = LoggerFactory.getLogger(PingMessageHandler.class);

    @Autowired
    private Messenger messenger;
    @Autowired
    private MessagePool messagePool;
    @Autowired
    private NodeConfig nodeConfig;

    @Override
    public void handleMessagePayload(PingPongMessage pingPongMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException, IOException {
        logger.trace("Received a ping message.");
        Message returnMessage = Message.Builder.builder()
                .setNetwork(nodeConfig.getNetwork())
                .setRequestType(CommandType.PONG)
                .setMessagePayload(pingPongMessage)
                .setChecksum(CryptoHasher.hash(pingPongMessage))
                .build();
        logger.trace("Returning a pong message.");
        messenger.sendTargetMessage(returnMessage, nodeConnection);
        this.messagePool.putMessage(returnMessage, nodeConnection);
    }
}
