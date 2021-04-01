package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;

@Component
public class PongMessageHandler implements MessageHandler<PingPongMessage> {
    @Override
    public MessagePayload handleMessagePayload(PingPongMessage pingPongMessage) {
        return null;
    }
}
