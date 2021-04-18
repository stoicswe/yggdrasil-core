package org.yggdrasil.node.network.messages;

import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.runners.MessagePoolRunner;
import org.yggdrasil.node.network.runners.NodeConnection;
import org.yggdrasil.node.network.exceptions.NodeDisconnectException;
import org.yggdrasil.node.network.messages.enums.NetworkType;
import org.yggdrasil.node.network.messages.enums.RequestType;
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
    private GetDataMessageHandler getDataMessageHandler;
    @Autowired
    private HeaderMessageHandler headerMessageHandler;
    @Autowired
    private HeaderPayloadMessageHandler headerPayloadMessageHandler;
    @Autowired
    private TransactionMessageHandler transactionMessageHandler;
    @Autowired
    private PingMessageHandler pingMessageHandler;
    @Autowired
    private PongMessageHandler pongMessageHandler;
    @Autowired
    private AddressResponseMessageHandler addressResponseHandler;
    @Autowired
    private GetAddressMessageHandler getAddressMessageHandler;
    @Autowired
    private HandshakeOfferMessageHandler handshakeOfferMessageHandler;
    @Autowired
    private HandshakeResponseMessageHandler handshakeResponseMessageHandler;

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
            switch (Objects.requireNonNull(RequestType.getByValue(message.getRequest()))) {
                case GET_DATA:
                    logger.info("Handling {} message.", RequestType.GET_DATA);
                    messagePayload = this.getDataMessageHandler.handleMessagePayload((GetDataMessage) message.getPayload(), nodeConnection);
                    break;
                case DATA_RESP:
                    logger.info("Handling {} message.", RequestType.DATA_RESP);
                    if (message.getPayload() instanceof HeaderMessage) {
                        messagePayload = this.headerMessageHandler.handleMessagePayload((HeaderMessage) message.getPayload(), nodeConnection);
                        returnMessage = Message.Builder.newBuilder()
                                .setNetwork(NetworkType.getByValue(message.getNetwork()))
                                .setRequestType(RequestType.ACKNOWLEDGE)
                                .setMessagePayload(messagePayload)
                                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                                .setChecksum(CryptoHasher.hash(messagePayload))
                                .build();
                    }
                    if (message.getPayload() instanceof HeaderPayload) {
                        messagePayload = this.headerPayloadMessageHandler.handleMessagePayload((HeaderPayload) message.getPayload(), nodeConnection);
                        returnMessage = Message.Builder.newBuilder()
                                .setNetwork(NetworkType.getByValue(message.getNetwork()))
                                .setRequestType(RequestType.ACKNOWLEDGE)
                                .setMessagePayload(messagePayload)
                                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                                .setChecksum(CryptoHasher.hash(messagePayload))
                                .build();
                    }
                    if (message.getPayload() instanceof TransactionMessage) {
                        messagePayload = this.transactionMessageHandler.handleMessagePayload((TransactionMessage) message.getPayload(), nodeConnection);
                        returnMessage = Message.Builder.newBuilder()
                                .setNetwork(NetworkType.getByValue(message.getNetwork()))
                                .setRequestType(RequestType.ACKNOWLEDGE)
                                .setMessagePayload(messagePayload)
                                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                                .setChecksum(CryptoHasher.hash(messagePayload))
                                .build();
                    }
                    break;
                case GET_ADDR:
                    logger.info("Handling {} message.", RequestType.GET_ADDR);
                    messagePayload = this.getAddressMessageHandler.handleMessagePayload((AddressMessage) message.getPayload(), nodeConnection);
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(RequestType.PONG)
                            .setMessagePayload(messagePayload)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case ADDR_RESP:
                    logger.info("Handling {} message.", RequestType.ADDR_RESP);
                    messagePayload = this.addressResponseHandler.handleMessagePayload((AddressPayload) message.getPayload(), nodeConnection);
                    break;
                case PING:
                    logger.info("Handling {} message.", RequestType.PING);
                    messagePayload = this.pingMessageHandler.handleMessagePayload((PingPongMessage) message.getPayload(), nodeConnection);
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(RequestType.PONG)
                            .setMessagePayload(messagePayload)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case PONG:
                    logger.info("Handling {} message.", RequestType.PONG);
                    this.pongMessageHandler.handleMessagePayload((PingPongMessage) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.newBuilder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(RequestType.ACKNOWLEDGE)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case HANDSHAKE_OFFR:
                    logger.info("Received {} message.", RequestType.HANDSHAKE_OFFR);
                    logger.debug("Ignoring the {} message (received outside of handshake).", RequestType.HANDSHAKE_OFFR);
                    break;
                case HANDSHAKE_RESP:
                    logger.info("Received {} message.", RequestType.HANDSHAKE_RESP);
                    logger.debug("Ignoring the {} message (received outside of handshake).", RequestType.HANDSHAKE_RESP);
                    break;
                case ACKNOWLEDGE:
                    logger.info("Handling {} message.", RequestType.ACKNOWLEDGE);
                    logger.debug("Acknowledgement received for the checksum: {}", CryptoHasher.humanReadableHash(message.getChecksum()));
                    AcknowledgeMessage ackPayload = (AcknowledgeMessage) message.getPayload();
                    Message mAck = this.messagePool.getMessage(ackPayload.getAcknowledgeChecksum()).getRight();
                    if(mAck != null){
                        this.messagePool.removeMessage(ackPayload.getAcknowledgeChecksum());
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
