package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.TransactionMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class TransactionMessageHandler implements MessageHandler<TransactionMessage> {

    @Autowired
    private MessagePool messagePool;

    @Override
    public MessagePayload handleMessagePayload(TransactionMessage transactionMessage, NodeConnection nodeConnection) {
        return null;
    }
}
