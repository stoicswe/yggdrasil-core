package org.yggdrasil.node.network.messages.handlers.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.PrefilledTransactionPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class PrefilledTransactionHandler implements MessageHandler<PrefilledTransactionPayload> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(PrefilledTransactionPayload prefilledTransactionPayload, NodeConnection nodeConnection) throws Exception {
        // not implemented
        return;
    }
}
