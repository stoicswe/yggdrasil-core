package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;

@Component
public class AddressResponseMessageHandler implements MessageHandler<AddressPayload> {

    @Override
    public MessagePayload handleMessagePayload(AddressPayload addressPayload) {
        return null;
    }

}
