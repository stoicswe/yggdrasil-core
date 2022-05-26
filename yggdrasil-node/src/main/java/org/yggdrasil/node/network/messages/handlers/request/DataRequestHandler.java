package org.yggdrasil.node.network.messages.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.InventoryVector;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;
import org.yggdrasil.node.network.messages.requests.DataMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

@Component
public class DataRequestHandler implements MessageHandler<DataMessageRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Mempool mempool;
    @Autowired
    private Blockchain blockchain;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;

    @Override
    public MessagePayload handleMessagePayload(DataMessageRequest dataMessageRequest, NodeConnection nodeConnection) throws Exception {

        MessagePayload response;

        if(dataMessageRequest.getRequestCount() == dataMessageRequest.getRequestedData().length) {
            for(InventoryVector v : dataMessageRequest.getRequestedData()) {
                switch (v.getType()) {
                    case ERROR:
                        break;
                    case MSG_TX:
                        response = TransactionPayload.Builder
                                .builder()
                                .setVersion()
                                .setWitnessFlag()
                                .setTxIns()
                                .setTxOuts()
                                .setWitnesses()
                                .setLockTime()
                                .build();
                        break;
                    case MSG_BLOCK:

                        break;
                    case MSG_FILTERED_BLOCK:
                        break;
                    case MSG_CMPCT_BLOCK:
                        break;
                    case MSG_WITNESS_TX:
                        break;
                    case MSG_WITNESS_BLOCK:
                        break;
                    case MSG_FILTERED_WITNESS_BLOCK:
                        break;
                }
            }
        }

        return null;
    }

}
