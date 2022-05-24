package org.yggdrasil.node.network.messages.handlers.response;

import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.TransactionWitness;
import org.yggdrasil.node.network.runners.NodeConnection;

public class TransactionWitnessHandler implements MessageHandler<TransactionWitness> {
    @Override
    public MessagePayload handleMessagePayload(TransactionWitness transactionWitness, NodeConnection nodeConnection) throws Exception {
        return null;
    }
}
