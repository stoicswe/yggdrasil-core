package org.yggdrasil.node.network.messages.handlers.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.AddressMessage;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;
import org.yggdrasil.node.network.peer.PeerRecord;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.List;

@Component
public class AddressRequestHandler implements MessageHandler<AddressMessage> {

    @Autowired
    private PeerRecordIndexer peerRecordIndexer;

    @Override
    public MessagePayload handleMessagePayload(AddressMessage addressMessage, NodeConnection nodeConnection) {
        if(addressMessage.getIpAddressCount() > 0) {
            List<PeerRecord> peerRecords;
            if(addressMessage.getIpAddressCount() > 25) {
                peerRecords = peerRecordIndexer.getPeerRecords(25);
            } else {
                peerRecords = peerRecordIndexer.getPeerRecords(addressMessage.getIpAddressCount());
            }
            return AddressMessage.Builder.newBuilder()
                    .setIpAddresses(peerRecords.stream().map(PeerRecord::toAddressPayload).toArray(AddressPayload[]::new))
                    .setIpAddressCount(peerRecords.size())
                    .build();
        }
        return AddressMessage.Builder.newBuilder()
                .setIpAddressCount(0)
                .setIpAddresses(new AddressPayload[0])
                .build();
    }
}
