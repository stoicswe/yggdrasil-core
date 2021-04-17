package org.yggdrasil.node.network.messages.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class PingMessageHandler implements MessageHandler<PingPongMessage> {

    private static final Logger logger = LoggerFactory.getLogger(PingMessageHandler.class);

    @Autowired
    private MessagePool messagePool;

    @Override
    public MessagePayload handleMessagePayload(PingPongMessage pingPongMessage, NodeConnection nodeConnection) {
        logger.trace("Received a ping message.");
        logger.trace("Returning a pong message.");
        return pingPongMessage;
    }
}
