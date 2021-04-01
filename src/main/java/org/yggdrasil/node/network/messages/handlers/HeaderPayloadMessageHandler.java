package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.HeaderPayload;

@Component
public class HeaderPayloadMessageHandler implements MessageHandler<HeaderPayload> {
    @Override
    public MessagePayload handleMessagePayload(HeaderPayload headerPayload) {
        return null;
    }
}
