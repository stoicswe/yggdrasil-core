package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.GetDataMessage;

@Component
public class GetDataMessageHandler implements MessageHandler<GetDataMessage> {

    @Override
    public MessagePayload handleMessagePayload(GetDataMessage getDataMessage) {
        return null;
    }
}
