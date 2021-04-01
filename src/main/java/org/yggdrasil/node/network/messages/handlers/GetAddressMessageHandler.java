package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.AddressMessage;

@Component
public class GetAddressMessageHandler implements MessageHandler<AddressMessage> {

    @Override
    public MessagePayload handleMessagePayload(AddressMessage addressMessage) {
        return null;
    }
}
