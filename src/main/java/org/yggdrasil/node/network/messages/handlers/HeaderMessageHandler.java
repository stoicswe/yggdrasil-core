package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.HeaderMessage;

@Component
public class HeaderMessageHandler implements MessageHandler<HeaderMessage> {
    @Override
    public MessagePayload handleMessagePayload(HeaderMessage headerMessage) {
        return null;
    }
}
