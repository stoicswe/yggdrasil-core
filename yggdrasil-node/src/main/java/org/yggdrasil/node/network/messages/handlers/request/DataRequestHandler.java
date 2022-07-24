package org.yggdrasil.node.network.messages.handlers.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.MessageHandler;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.messages.requests.DataMessageRequest;
import org.yggdrasil.node.network.runners.NodeConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private MessagePool messagePool;

    @Override
    public void handleMessagePayload(DataMessageRequest dataMessageRequest, NodeConnection nodeConnection) throws Exception {

        Message message;
        MessagePayload messagePayload;

        List<TransactionPayload> mempoolTxns = null;
        List<BlockHeaderPayload> blockHeaders = null;

        if(dataMessageRequest.getRequestCount() == dataMessageRequest.getRequestedData().length) {
            for(InventoryVector v : dataMessageRequest.getRequestedData()) {
                switch (v.getType()) {
                    case ERROR:
                        break;
                    case MSG_TX:
                        if(mempoolTxns == null) mempoolTxns = new ArrayList<>();
                        Transaction mTxn = mempool.peekTransaction(v.getHash());
                        mempoolTxns.add(TransactionPayload.Builder.builder()
                                        .setVersion(Blockchain._VERSION)
                                        //.setWitnessFlag(mTxn.isWitness())
                                        //.setTxIns()
                                        //.setTxOuts()
                                        //.setWitnesses()
                                        //.setLockTime()
                                .build());
                        break;
                    case MSG_BLOCK:
                        if(blockHeaders == null) blockHeaders = new ArrayList<>();
                        Optional<Block> block = blockchain.getBlock(v.getHash());
                        if (block.isPresent()) blockHeaders.add(BlockHeaderPayload.Builder.builder()
                                        .setVersion(Blockchain._VERSION)
                                        .setPreviousHash(block.get().getHeader().getPreviousBlockHash())
                                        .setMerkleRoot(block.get().getHeader().getMerkleRoot())
                                        .setTimestamp((int) block.get().getHeader().getEpochTime())
                                        .setDiff(block.get().getHeader().getDiff())
                                        .setNonce(block.get().getHeader().getNonce())
                                .build());
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

        if(mempoolTxns != null && mempoolTxns.size() > 0) {
            messagePayload = MempoolTransactionPayload.Builder.builder()
                    .setTransactions(mempoolTxns.toArray(TransactionPayload[]::new))
                    .setRequestChecksum(CryptoHasher.hash(dataMessageRequest))
                    .build();
            message = Message.Builder.builder()
                    .setNetwork(nodeConfig.getNetwork())
                    .setRequestType(CommandType.MEMPOOL_TXN_PAYLOAD)
                    .setMessagePayload(messagePayload)
                    .setChecksum(CryptoHasher.hash(messagePayload))
                    .build();
            logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
            messenger.sendTargetMessage(message, nodeConnection);
        }

        if(blockHeaders != null && blockHeaders.size() > 0) {
            messagePayload = BlockHeaderResponsePayload.Builder.builder()
                    .setHeaders(blockHeaders.toArray(BlockHeaderPayload[]::new))
                    .setRequestChecksum(CryptoHasher.hash(dataMessageRequest))
                    .build();
            message = Message.Builder.builder()
                    .setNetwork(nodeConfig.getNetwork())
                    .setRequestType(CommandType.BLOCK_HEADER_PAYLOAD)
                    .setMessagePayload(messagePayload)
                    .setChecksum(CryptoHasher.hash(messagePayload))
                    .build();
            logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
            messenger.sendTargetMessage(message, nodeConnection);
        }
    }
}
