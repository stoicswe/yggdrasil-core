package org.yggdrasil.node.network.messages.handlers;

import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.TransactionMessage;

@Component
public class TransactionMessageHandler implements MessageHandler<TransactionMessage> {
    @Override
    public MessagePayload handleMessagePayload(TransactionMessage transactionMessage) {
        return null;
    }
}
