package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.AddressMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class GetAddressMessageHandler implements MessageHandler<AddressMessage> {

    @Autowired
    private MessagePool messagePool;

    @Override
    public MessagePayload handleMessagePayload(AddressMessage addressMessage, NodeConnection nodeConnection) {
        return null;
    }
}
