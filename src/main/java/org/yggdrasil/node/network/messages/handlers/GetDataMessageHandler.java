package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.enums.GetDataType;
import org.yggdrasil.node.network.messages.enums.HeaderType;
import org.yggdrasil.node.network.messages.enums.RequestType;
import org.yggdrasil.node.network.messages.payloads.GetDataMessage;
import org.yggdrasil.node.network.messages.payloads.HeaderMessage;
import org.yggdrasil.node.network.messages.payloads.HeaderPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class GetDataMessageHandler implements MessageHandler<GetDataMessage> {

    @Autowired
    private MessagePool messagePool;
    @Autowired
    private Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(GetDataMessage getDataMessage, NodeConnection nodeConnection) {

        MessagePayload messagePayload;

        if(getDataMessage.getHashCount() == getDataMessage.getObjectHashes().length) {
            switch (Objects.requireNonNull(GetDataType.getByValue(getDataMessage.getType()))) {
                case BLOCK:
                    List<HeaderPayload> headers = new ArrayList<>();
                    for(byte[] blockHash : getDataMessage.getObjectHashes()) {
                        List<Block> bs = blockchain.getBlock(blockHash);
                        if(bs.size() > 0) {
                            Block b = bs.get(0);
                            HeaderPayload hp = HeaderPayload.Builder.newBuilder()
                                    .setHash(b.getBlockHash())
                                    .setNonce(b.getNonce())
                                    .setPreviousHash(b.getPreviousBlockHash())
                                    .setTime((int) b.getTimestamp().toEpochSecond())
                                    .setTransactionCount((b.getData() instanceof Object[]) ? ((Object[]) b.getData()).length : -1)
                                    .build();
                            headers.add(hp);
                        }
                    }
                    messagePayload = HeaderMessage.Builder.newBuilder()
                            .setHeaderType(HeaderType.BLOCK_HEADER)
                            .setHeaderCount(headers.size())
                            .setHeaders((HeaderPayload[]) headers.toArray())
                            .build();
                    break;
                case BLOCKCHAIN:
                    
                    break;
                case TRANSACTION:

                    break;
                case MEMPOOL:

                    break;
                default:
                    break;
            }
        } else {
            throw new InvalidMessageException("Message received reported wrong hash count versus data provided.");
        }
        return messagePayload;
    }

}
