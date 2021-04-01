package org.yggdrasil.node.network.messages.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;

@Component
public class PingMessageHandler implements MessageHandler<PingPongMessage> {

    private static final Logger logger = LoggerFactory.getLogger(PingMessageHandler.class);

    @Override
    public MessagePayload handleMessagePayload(PingPongMessage pingPongMessage) {
        logger.trace("Received a ping message.");
        logger.trace("Returning a pong message.");
        return pingPongMessage;
    }
}
