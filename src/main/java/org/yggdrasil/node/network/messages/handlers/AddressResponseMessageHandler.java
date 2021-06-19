package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.AddressMessage;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;
import org.yggdrasil.node.network.peer.PeerRecord;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;

@Component
public class AddressResponseMessageHandler implements MessageHandler<AddressMessage> {

    @Autowired
    private PeerRecordIndexer peerRecordIndexer;

    @Override
    public MessagePayload handleMessagePayload(AddressMessage addressMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException {
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

        return AcknowledgeMessage.Builder.newBuilder()
                .setAcknowledgeChecksum(CryptoHasher.hash(addressMessage))
                .build();
    }

}
