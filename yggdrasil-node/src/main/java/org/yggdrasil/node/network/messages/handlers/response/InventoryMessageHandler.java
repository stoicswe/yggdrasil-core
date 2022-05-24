package org.yggdrasil.node.network.messages.handlers.response;

import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.InventoryMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

public class InventoryMessageHandler implements MessageHandler<InventoryMessage> {
    @Override
    public MessagePayload handleMessagePayload(InventoryMessage inventoryMessage, NodeConnection nodeConnection) throws Exception {
        return null;
    }
}
