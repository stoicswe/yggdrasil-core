package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.GetDataMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class GetDataMessageHandler implements MessageHandler<GetDataMessage> {

    @Autowired
    private MessagePool messagePool;

    @Override
    public MessagePayload handleMessagePayload(GetDataMessage getDataMessage, NodeConnection nodeConnection) {
        return null;
    }
}
