package org.yggdrasil.node.network.messages.handlers.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.AddressMessage;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;
import org.yggdrasil.node.network.peer.PeerRecord;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
public class AddressRequestHandler implements MessageHandler<AddressMessage> {

    @Autowired
    private PeerRecordIndexer peerRecordIndexer;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;

    @Override
    public void handleMessagePayload(AddressMessage addressMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException, IOException {
        if(addressMessage.getIpAddressCount() > 0) {
            List<PeerRecord> peerRecords;
            if(addressMessage.getIpAddressCount() > 25) {
                peerRecords = peerRecordIndexer.getPeerRecords(25);
            } else {
                peerRecords = peerRecordIndexer.getPeerRecords(addressMessage.getIpAddressCount());
            }

            MessagePayload messagePayload = AddressMessage.Builder.newBuilder()
                            .setIpAddresses(peerRecords.stream().map(PeerRecord::toAddressPayload).toArray(AddressPayload[]::new))
                            .setIpAddressCount(peerRecords.size())
                            .build();
            Message message = Message.Builder.builder()
                    .setNetwork(nodeConfig.getNetwork())
                    .setRequestType(CommandType.ADDRESS_PAYLOAD)
                    .setMessagePayload(messagePayload)
                    .setChecksum(CryptoHasher.hash(messagePayload))
                    .build();

            messenger.sendTargetMessage(message, nodeConnection);
        }
    }
}
