package org.yggdrasil.node.network.messages.handlers.response;

import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.BlockTransactions;
import org.yggdrasil.node.network.runners.NodeConnection;

public class BlockTransactionsHandler implements MessageHandler<BlockTransactions> {

    @Override
    public MessagePayload handleMessagePayload(BlockTransactions blockTransactions, NodeConnection nodeConnection) throws Exception {


        return null;
    }

}
