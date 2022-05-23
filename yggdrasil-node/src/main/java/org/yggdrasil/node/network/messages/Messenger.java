package org.yggdrasil.node.network.messages;

import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.messages.handlers.util.AddressRequestHandler;
import org.yggdrasil.node.network.messages.handlers.request.BlockRequestHandler;
import org.yggdrasil.node.network.messages.handlers.util.AddressMessageHandler;
import org.yggdrasil.node.network.messages.handlers.response.BlockHeaderMessageHandler;
import org.yggdrasil.node.network.messages.handlers.response.BlockMessageHandler;
import org.yggdrasil.node.network.messages.handlers.util.PingMessageHandler;
import org.yggdrasil.node.network.messages.requests.BlockMessageRequest;
import org.yggdrasil.node.network.runners.MessagePoolRunner;
import org.yggdrasil.node.network.runners.NodeConnection;
import org.yggdrasil.node.network.exceptions.NodeDisconnectException;
import org.yggdrasil.node.network.messages.enums.NetworkType;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.*;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.messages.validators.MessageValidator;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Timer;

/**
 * The Messenger component will handle the messages from other nodes
 * and the sending of messages to other nodes.
 *
 * @since 0.0.11
 * @author nathanielbunch
 */
@Component
public class Messenger {

    private final Logger logger = LoggerFactory.getLogger(Messenger.class);

    @Autowired
    private Node node;
    @Autowired
    private MessagePool messagePool;
    // Timer for checking expired messages
    private Timer messagePoolRunnerTimer;
    @Autowired
    private MessageValidator validator;
    @Autowired
    private BlockRequestHandler blockRequestHandler;
    @Autowired
    private BlockMessageHandler blockMessageHandler;
    @Autowired
    private BlockHeaderMessageHandler blockHeaderMessageHandler;
    @Autowired
    private MempoolTransactionMessageHandler basicTransactionMessageHandler;
    @Autowired
    private TransactionMessageHandler transactionMessageHandler;
    @Autowired
    private PingMessageHandler pingMessageHandler;
    @Autowired
    private AddressMessageHandler addressResponseHandler;
    @Autowired
    private AddressRequestHandler addressRequestHandler;

    @PostConstruct
    private void init() {
        this.messagePoolRunnerTimer = new Timer();
        //start the scheduled task in 30 seconds of checking messagePool for expired messages every 30 seconds
        this.messagePoolRunnerTimer.schedule(new MessagePoolRunner(this, this.messagePool), 30000, 30000);
    }

    public MessageValidator getValidator() {
        return this.validator;
    }

    public void sendTargetMessage(Message message, String nodeIdentifier) throws NodeDisconnectException, IOException, NoSuchAlgorithmException {
        if(nodeIdentifier != null) {
            NodeConnection nodeConnection = this.node.getConnectedNodes().get(nodeIdentifier);
            this.sendTargetMessage(message, nodeConnection);
        } else {
            logger.debug("Message was not sent because the nodeIdentifier passed was null.");
        }
    }

    public void sendTargetMessage(Message message, NodeConnection nodeConnection) throws NodeDisconnectException, IOException, NoSuchAlgorithmException {
        this.validator.isValidMessage(message);
        if(nodeConnection != null) {
            if(nodeConnection.isConnected()) {
                nodeConnection.getNodeOutput().writeObject(message);
            } else {
                node.getConnectedNodes().remove(nodeConnection.getNodeIdentifier());
                throw new NodeDisconnectException(String.format("Peer %s was disconnected and message could not be transmitted.", nodeConnection.getNodeIdentifier()));
            }
        } else {
            logger.debug("Message not sent because the target peer was passed as null.");
        }
    }

    public void sendBroadcastMessage(Message message) throws IOException, NoSuchAlgorithmException {
        this.validator.isValidMessage(message);
        for(String nck : node.getConnectedNodes().keySet()) {
            NodeConnection nc = node.getConnectedNodes().get(nck);
            if(nc.isConnected()) {
                try {
                    logger.info("Broadcasting message: {} to: {}", message.toString(), nc.getNodeIdentifier());
                    nc.getNodeOutput().writeObject(message);
                } catch (Exception e){
                    logger.debug("Removing bad peer connection: {}", nck);
                    node.getConnectedNodes().remove(nck);
                }
            } else {
                node.getConnectedNodes().remove(nck);
                logger.debug("Message not sent to {} because the target peer was disconnected.", nck);
            }
        }
    }

    public void handleMessage(Message message, NodeConnection nodeConnection) {
        logger.info("In handleMessage.");
        Message returnMessage = null;
        MessagePayload messagePayload = null;
        try {
            this.validator.isValidMessage(message);
            logger.info("Handling valid message.");
            // This should never be null, since the message is validated before it is handled.
            switch (Objects.requireNonNull(CommandType.getByValue(message.getCommand()))) {
                case REQUEST_BLOCK_HEADER:
                    logger.info("Handling {} message.", CommandType.REQUEST_BLOCK_HEADER);
                    messagePayload = this.blockRequestHandler.handleMessagePayload((BlockMessageRequest) message.getPayload(), nodeConnection);
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(CommandType.INVENTORY_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case INVENTORY_PAYLOAD:
                    logger.info("Handling {} message.", CommandType.INVENTORY_PAYLOAD);
                    if (message.getPayload() instanceof BlockHeaderResponsePayload) {
                        logger.info("{} message is a BlockchainMessage.", CommandType.INVENTORY_PAYLOAD);
                        this.blockHeaderMessageHandler.handleMessagePayload((BlockHeaderResponsePayload) message.getPayload(), nodeConnection);
                        messagePayload = AcknowledgeMessage.Builder.newBuilder()
                                .setAcknowledgeChecksum(message.getChecksum())
                                .build();
                        returnMessage = Message.Builder.newBuilder()
                                .setNetwork(NetworkType.getByValue(message.getNetwork()))
                                .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                                .setMessagePayload(messagePayload)
                                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                                .setChecksum(CryptoHasher.hash(messagePayload))
                                .build();
                    }
                    if (message.getPayload() instanceof BlockMessage) {
                        logger.info("{} message is a BlockMessage", CommandType.INVENTORY_PAYLOAD);
                        this.blockMessageHandler.handleMessagePayload((BlockMessage) message.getPayload(), nodeConnection);
                        messagePayload = AcknowledgeMessage.Builder.newBuilder()
                                .setAcknowledgeChecksum(message.getChecksum())
                                .build();
                        returnMessage = Message.Builder.newBuilder()
                                .setNetwork(NetworkType.getByValue(message.getNetwork()))
                                .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                                .setMessagePayload(messagePayload)
                                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                                .setChecksum(CryptoHasher.hash(messagePayload))
                                .build();
                    }
                    if (message.getPayload() instanceof MempoolTransactionMessage) {
                        logger.info("{} message is a BasicTransactionMessage.", CommandType.INVENTORY_PAYLOAD);
                        this.basicTransactionMessageHandler.handleMessagePayload((MempoolTransactionMessage) message.getPayload(), nodeConnection);
                        messagePayload = AcknowledgeMessage.Builder.newBuilder()
                                .setAcknowledgeChecksum(message.getChecksum())
                                .build();
                        returnMessage = Message.Builder.newBuilder()
                                .setNetwork(NetworkType.getByValue(message.getNetwork()))
                                .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                                .setMessagePayload(messagePayload)
                                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                                .setChecksum(CryptoHasher.hash(messagePayload))
                                .build();
                    }
                    if (message.getPayload() instanceof TransactionMessage) {
                        logger.info("{} message is a TransactionMessage.", CommandType.INVENTORY_PAYLOAD);
                        this.transactionMessageHandler.handleMessagePayload((TransactionMessage) message.getPayload(), nodeConnection);
                        messagePayload = AcknowledgeMessage.Builder.newBuilder()
                                .setAcknowledgeChecksum(message.getChecksum())
                                .build();
                        returnMessage = Message.Builder.newBuilder()
                                .setNetwork(NetworkType.getByValue(message.getNetwork()))
                                .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                                .setMessagePayload(messagePayload)
                                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                                .setChecksum(CryptoHasher.hash(messagePayload))
                                .build();
                    }
                    break;
                case REQUEST_ADDRESS:
                    logger.info("Handling {} message.", CommandType.REQUEST_ADDRESS);
                    // Return an AddressMessage, with AddressPayloads
                    messagePayload = this.addressRequestHandler.handleMessagePayload((AddressMessage) message.getPayload(), nodeConnection);
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(CommandType.ADDRESS_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case ADDRESS_PAYLOAD:
                    logger.info("Handling {} message.", CommandType.ADDRESS_PAYLOAD);
                    this.addressResponseHandler.handleMessagePayload((AddressMessage) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.newBuilder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case PING:
                    logger.info("Handling {} message.", CommandType.PING);
                    messagePayload = this.pingMessageHandler.handleMessagePayload((PingPongMessage) message.getPayload(), nodeConnection);
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(CommandType.PONG)
                            .setMessagePayload(messagePayload)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case PONG:
                    logger.info("Handling {} message.", CommandType.PONG);
                    messagePayload = AcknowledgeMessage.Builder.newBuilder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case HANDSHAKE_OFFR:
                    logger.info("Received {} message.", CommandType.HANDSHAKE_OFFR);
                    logger.debug("Ignoring the {} message (received outside of handshake).", CommandType.HANDSHAKE_OFFR);
                    break;
                case HANDSHAKE_RESP:
                    logger.info("Received {} message.", CommandType.HANDSHAKE_RESP);
                    logger.debug("Ignoring the {} message (received outside of handshake).", CommandType.HANDSHAKE_RESP);
                    break;
                case ACKNOWLEDGE_PAYLOAD:
                    logger.info("Handling {} message.", CommandType.ACKNOWLEDGE_PAYLOAD);
                    logger.debug("Acknowledgement received for the checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
                    AcknowledgeMessage ackPayload = (AcknowledgeMessage) message.getPayload();
                    ExpiringMessageRecord emr = this.messagePool.getMessage(ackPayload.getAcknowledgeChecksum());
                    if(emr != null) {
                        Message mAck = emr.getRight();
                        if (mAck != null) {
                            this.messagePool.removeMessage(ackPayload.getAcknowledgeChecksum());
                        }
                    } else {
                        logger.debug("Received an acknowledgement for nonexistent message, maybe other node is confused?");
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.warn("Error while processing message: [{}] -> [{}].", message.toString(), e.getMessage());
        }

        if(returnMessage != null) {
            try {
                // write the return message back to the nodeconnection
                this.validator.isValidMessage(returnMessage);
                this.sendTargetMessage(returnMessage, nodeConnection);
                this.messagePool.putMessage(returnMessage, nodeConnection);
            } catch (IOException | NoSuchAlgorithmException e) {
                logger.error("Error while sending response message: {}.", e.getMessage());
            }
        }
    }

}
