package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.AddressMessage;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class AddressResponseMessageHandler implements MessageHandler<AddressMessage> {

    @Autowired
    private MessagePool messagePool;

    @Override
    public MessagePayload handleMessagePayload(AddressMessage addressMessage, NodeConnection nodeConnection) {
        // if the count of IPs does not match the length of addressMessages in the message
        // then send an acknowledgement and ignore the payload data
        
        return null;
    }

}
