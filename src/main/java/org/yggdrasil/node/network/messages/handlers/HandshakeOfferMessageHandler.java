package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.HandshakeMessage;

@Component
public class HandshakeOfferMessageHandler implements MessageHandler<HandshakeMessage> {
    @Override
    public MessagePayload handleMessagePayload(HandshakeMessage handshakeMessage) {
        return null;
    }
}
