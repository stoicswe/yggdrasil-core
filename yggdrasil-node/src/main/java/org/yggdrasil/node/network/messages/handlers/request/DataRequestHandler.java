package org.yggdrasil.node.network.messages.handlers.request;

import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.requests.DataMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

public class DataRequestHandler implements MessageHandler<DataMessageRequest> {

    @Override
    public MessagePayload handleMessagePayload(DataMessageRequest dataMessageRequest, NodeConnection nodeConnection) throws Exception {
        return null;
    }

}
