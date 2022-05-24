package org.yggdrasil.node.network.messages.handlers.response;

import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.PrefilledTransactionPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

public class PrefilledTransactionHandler implements MessageHandler<PrefilledTransactionPayload> {
    @Override
    public MessagePayload handleMessagePayload(PrefilledTransactionPayload prefilledTransactionPayload, NodeConnection nodeConnection) throws Exception {
        return null;
    }
}
