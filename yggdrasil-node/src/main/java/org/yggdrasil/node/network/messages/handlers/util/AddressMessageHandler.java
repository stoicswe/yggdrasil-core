package org.yggdrasil.node.network.messages.handlers.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.AddressMessage;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;
import org.yggdrasil.node.network.peer.PeerRecord;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;

@Component
public class AddressMessageHandler implements MessageHandler<AddressMessage> {

    @Autowired
    private PeerRecordIndexer peerRecordIndexer;
    @Autowired
    private NodeConfig nodeConfig;

    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(AddressMessage addressMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException {
        // if the count of IPs does not match the length of addressMessages in the message
        // then send an acknowledgement and ignore the payload data
        if(addressMessage.getIpAddressCount() == addressMessage.getIpAddresses().length) {
            for(AddressPayload addressPayload : addressMessage.getIpAddresses()) {
                // add connections to the active connections and store connection data
                PeerRecord peerRecord = PeerRecord.Builder.newBuilder()
                        .buildFromAddressPayload(addressPayload);
                this.peerRecordIndexer.addPeerRecord(peerRecord);
            }
        }
    }

}
