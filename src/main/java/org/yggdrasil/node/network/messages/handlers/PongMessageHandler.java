package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class PongMessageHandler implements MessageHandler<PingPongMessage> {

    @Autowired
    private MessagePool messagePool;

    @Override
    public MessagePayload handleMessagePayload(PingPongMessage pingPongMessage, NodeConnection nodeConnection) {
        return null;
    }
}
