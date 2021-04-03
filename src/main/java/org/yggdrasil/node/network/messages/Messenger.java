package org.yggdrasil.node.network.messages;

import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.NodeConnection;
import org.yggdrasil.node.network.exceptions.NodeDisconnectException;
import org.yggdrasil.node.network.messages.enums.NetworkType;
import org.yggdrasil.node.network.messages.enums.RequestType;
import org.yggdrasil.node.network.messages.handlers.*;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.messages.validators.MessageValidator;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;

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

    public Message handleMessage(Message message) {
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
                    messagePayload = this.getDataMessageHandler.handleMessagePayload((GetDataMessage) message.getPayload());
                    break;
                case DATA_RESP:
                    logger.info("Handling {} message.", RequestType.DATA_RESP);
                    if (message.getPayload() instanceof HeaderMessage) {
                        messagePayload = this.headerMessageHandler.handleMessagePayload((HeaderMessage) message.getPayload());
                    }
                    if (message.getPayload() instanceof HeaderPayload) {
                        messagePayload = this.headerPayloadMessageHandler.handleMessagePayload((HeaderPayload) message.getPayload());
                    }
                    if (message.getPayload() instanceof TransactionMessage) {
                        messagePayload = this.transactionMessageHandler.handleMessagePayload((TransactionMessage) message.getPayload());
                    }
                    break;
                case GET_ADDR:
                    logger.info("Handling {} message.", RequestType.GET_ADDR);
                    messagePayload = this.getAddressMessageHandler.handleMessagePayload((AddressMessage) message.getPayload());
                    break;
                case ADDR_RESP:
                    logger.info("Handling {} message.", RequestType.ADDR_RESP);
                    messagePayload = this.addressResponseHandler.handleMessagePayload((AddressPayload) message.getPayload());
                    break;
                case PING:
                    logger.info("Handling {} message.", RequestType.PING);
                    messagePayload = this.pingMessageHandler.handleMessagePayload((PingPongMessage) message.getPayload());
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
                    this.pongMessageHandler.handleMessagePayload((PingPongMessage) message.getPayload());
                    messagePayload = AcknowledgeMessage.Builder.newBuilder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.newBuilder()
                            .setNetwork(NetworkType.getByValue(message.getNetwork()))
                            .setRequestType(RequestType.ACKNOWLEDGE)
                            .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(messagePayload).totalSize()))
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case HANDSHAKE_OFFR:
                    logger.info("Handling {} message.", RequestType.HANDSHAKE_OFFR);
                    messagePayload = this.handshakeOfferMessageHandler.handleMessagePayload((HandshakeMessage) message.getPayload());
                    break;
                case HANDSHAKE_RESP:
                    logger.info("Handling {} message.", RequestType.HANDSHAKE_RESP);
                    messagePayload = this.handshakeResponseMessageHandler.handleMessagePayload((HandshakeMessage) message.getPayload());
                    break;
                case ACKNOWLEDGE:
                    logger.info("Handling {} message.", RequestType.ACKNOWLEDGE);
                    // TBH...not really sure how to handle this yet...
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.warn("Error while processing message: [{}] -> [{}]", message.toString(), e.getMessage());
        }
        return returnMessage;
    }

    public void sendTargetMessage(String target, Message message) throws IOException {
        NodeConnection nc = node.getConnectedNodes().get(target);
        if(nc != null) {
            if(nc.isConnected()) {
                nc.getNodeOutput().writeObject(message);
            } else {
                node.getConnectedNodes().remove(target);
                throw new NodeDisconnectException(String.format("Peer %s was disconnected and message could not be transmitted.", target));
            }
        } else {
            logger.warn("Target peer does not exist.");
        }
    }

    public void sendBroadcastMessage(Message message) throws IOException {
        for(String nck : node.getConnectedNodes().keySet()) {
            NodeConnection nc = node.getConnectedNodes().get(nck);
            if(nc.isConnected()) {
                nc.getNodeOutput().writeObject(message);
            } else {
                node.getConnectedNodes().remove(nck);
            }
        }
    }

}
