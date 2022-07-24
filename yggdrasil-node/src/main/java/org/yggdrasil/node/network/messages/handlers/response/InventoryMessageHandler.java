package org.yggdrasil.node.network.messages.handlers.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.InventoryMessage;
import org.yggdrasil.node.network.messages.payloads.InventoryVector;
import org.yggdrasil.node.network.messages.requests.DataMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryMessageHandler implements MessageHandler<InventoryMessage> {

    @Autowired
    private Mempool mempool;
    @Autowired
    private Blockchain blockchain;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(InventoryMessage inventoryMessage, NodeConnection nodeConnection) throws Exception {

        MessagePayload messagePayload = null;

        if(inventoryMessage.getCount() == inventoryMessage.getInventory().length) {
            List<InventoryVector> vs = new ArrayList<>();
            for(InventoryVector v : inventoryMessage.getInventory()) {
                switch (v.getType()){
                    case ERROR:
                        // Error cases are ignored.
                        break;
                    case MSG_TX:
                        if(mempool.peekTransaction(v.getHash()) == null) vs.add(v);
                        break;
                    case MSG_BLOCK:
                        if(blockchain.getBlock(v.getHash()).isEmpty()) vs.add(v);
                        break;
                    case MSG_FILTERED_BLOCK:
                        // Not used yet
                        break;
                    case MSG_CMPCT_BLOCK:
                        // Not used yet
                        break;
                    case MSG_WITNESS_TX:
                        // Not used yet
                        break;
                    case MSG_WITNESS_BLOCK:
                        // Not used yet
                        break;
                    case MSG_FILTERED_WITNESS_BLOCK:
                        // Not used yet
                        break;
                }
            }

            if(vs.size() > 0) {
                messagePayload = DataMessageRequest.Builder.builder()
                        .setRequestedData(vs.toArray(InventoryVector[]::new))
                        .build();
                Message m = Message.Builder.builder()
                        .setNetwork(nodeConfig.getNetwork())
                        .setRequestType(CommandType.REQUEST_BLOCK_HEADER)
                        .setMessagePayload(messagePayload)
                        .setChecksum(CryptoHasher.hash(messagePayload))
                        .build();

                messenger.sendTargetMessage(m, nodeConnection);
            }
        }
    }
}
