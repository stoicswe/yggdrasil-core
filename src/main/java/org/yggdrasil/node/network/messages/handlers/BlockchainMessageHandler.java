package org.yggdrasil.node.network.messages.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.BlockchainMessage;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlockchainMessageHandler implements MessageHandler<BlockchainMessage> {

    @Autowired
    Blockchain blockchain;

    @Override
    public MessagePayload handleMessagePayload(BlockchainMessage blockchainMessage, NodeConnection nodeConnection) throws NoSuchAlgorithmException {

        if(blockchainMessage.getHeaders().length == blockchainMessage.getHeaderCount()) {
            List<Block> blcks = new ArrayList<>();
            for(BlockHeaderPayload hp : blockchainMessage.getHeaders()) {
                Block blck = Block.Builder.newBuilder().buildFromBlockHeaderMessage(hp);
                if(blockchain.getBlock(blck.getBlockHash()).isEmpty()) {
                    blcks.add(blck);
                }
            }
            try {
                blockchain.addBlocks(blcks);
            } catch (CloneNotSupportedException e) {
                // do nothing
            }
        }

        return AcknowledgeMessage.Builder.newBuilder()
                .setAcknowledgeChecksum(CryptoHasher.hash(blockchainMessage))
                .build();

    }
}
