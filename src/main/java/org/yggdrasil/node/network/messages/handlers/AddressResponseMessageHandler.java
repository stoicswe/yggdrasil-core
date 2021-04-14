package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class AddressResponseMessageHandler implements MessageHandler<AddressPayload> {

    @Autowired
    private MessagePool messagePool;

    @Override
    public MessagePayload handleMessagePayload(AddressPayload addressPayload, NodeConnection nodeConnection) {
        return null;
    }

}
