package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.BlockMessage;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;

@Component
public class BlockMessageHandler implements MessageHandler<BlockMessage>{

    @Autowired
    Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(BlockMessage blockMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException {
        return null;
    }

}
