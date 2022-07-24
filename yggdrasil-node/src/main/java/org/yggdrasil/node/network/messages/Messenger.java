package org.yggdrasil.node.network.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.messages.enums.RejectCodeType;
import org.yggdrasil.node.network.runners.MessagePoolRunner;
import org.yggdrasil.node.network.runners.NodeConnection;
import org.yggdrasil.node.network.exceptions.NodeDisconnectException;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.handlers.*;
import org.yggdrasil.node.network.messages.payloads.*;
import org.yggdrasil.node.network.messages.validators.MessageValidator;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
    List<MessageHandler> messageHandlers;

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

    public void handleMessage(Message message, NodeConnection nodeConnection) throws NoSuchAlgorithmException {
        logger.info("In handleMessage.");
        Message returnMessage = null;
        MessagePayload messagePayload = null;
        try {
            this.validator.isValidMessage(message);
            logger.info("Handling valid message.");
            // This should never be null, since the message is validated before it is handled.
            // Get the appropriate handler (no idea if this works yet....)
            MessageHandler handle = this.messageHandlers.stream()
                    .filter(h -> Arrays.stream(h.getClass().getTypeParameters())
                            .anyMatch(c -> c.equals(message.getClass()))).findFirst().get();
            // Switch based on the message type, since responses can differ
            logger.info("Handling {} message.", message.getCommand());
            switch (Objects.requireNonNull(message.getCommand())) {
                case REQUEST_BLOCK_HEADER:
                case REQUEST_BLOCK:
                case REQUEST_BLOCK_TXNS:
                    handle.handleMessagePayload(message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case REQUEST_MEMPOOL_TXNS:
                case REQUEST_MEMPOOL_LATEST:
                    // do some logic to get the messages from the mempool
                    break;
                case REQUEST_ADDRESS:
                    logger.info("Handling {} message.", CommandType.REQUEST_ADDRESS);
                    // Return an AddressMessage, with AddressPayloads
                    handle.handleMessagePayload((AddressMessage) message.getPayload(), nodeConnection);
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ADDRESS_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
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
                case ADDRESS_PAYLOAD:
                    logger.info("Handling {} message.", CommandType.ADDRESS_PAYLOAD);
                    handle.handleMessagePayload((AddressMessage) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case BLOCK_HEADER_PAYLOAD:
                    handle.handleMessagePayload((BlockHeaderPayload) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case BLOCK_PAYLOAD:
                    handle.handleMessagePayload((BlockMessage) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case BLOCK_TXN_PAYLOAD:
                    handle.handleMessagePayload((BlockTransactions) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case INVENTORY_PAYLOAD:
                    handle.handleMessagePayload((InventoryMessage) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case PREFILLED_TXN_PAYLOAD:
                    // Not used.
                    break;
                case TRANSACTION_PAYLOAD:
                    handle.handleMessagePayload((TransactionPayload) message.getPayload(), nodeConnection);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case TXN_WITNESS_PAYLOAD:
                    // Not used
                    break;
                case NOT_FOUND_PAYLOAD:
                    // Do some more error handling here, since there needs to be better handling if there is no data found
                    // add logic to be able to search out a different peer for data
                    NotFoundResponsePayload nfr = (NotFoundResponsePayload) message.getPayload();
                    ExpiringMessageRecord em_nfr = this.messagePool.getMessage(nfr.getChecksum());
                    if(em_nfr != null) {
                        Message mAck = em_nfr.getRight();
                        if (mAck != null) {
                            this.messagePool.removeMessage(nfr.getChecksum());
                        }
                    } else {
                        logger.debug("Received a not found response for nonexistent message, maybe other node is confused?");
                    }
                    break;
                case REJECT_PAYLOAD:
                    // Do better error handling
                    RejectMessagePayload rm = (RejectMessagePayload) message.getPayload();
                    ExpiringMessageRecord em_rm = this.messagePool.getMessage(rm.getData());
                    if(em_rm != null) {
                        Message mAck = em_rm.getRight();
                        if (mAck != null) {
                            this.messagePool.removeMessage(rm.getData());
                        }
                    } else {
                        logger.debug("Received a reject for nonexistent message, maybe other node is confused?");
                    }
                    break;
                case PING:
                    logger.info("Handling {} message.", CommandType.PING);
                    handle.handleMessagePayload((PingPongMessage) message.getPayload(), nodeConnection);
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.PONG)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case PONG:
                    logger.info("Handling {} message.", CommandType.PONG);
                    messagePayload = AcknowledgeMessage.Builder.builder()
                            .setAcknowledgeChecksum(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.ACKNOWLEDGE_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                case HANDSHAKE_OFFR:
                case HANDSHAKE_RESP:
                    messagePayload = RejectMessagePayload.Builder.builder()
                            .setRejectCode(RejectCodeType.REJECT_INVALID)
                            .setMessage("Message type received in invalid runtime block.")
                            .setData(message.getChecksum())
                            .build();
                    returnMessage = Message.Builder.builder()
                            .setNetwork(message.getNetwork())
                            .setRequestType(CommandType.REJECT_PAYLOAD)
                            .setMessagePayload(messagePayload)
                            .setChecksum(CryptoHasher.hash(messagePayload))
                            .build();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.warn("Error while processing message: [{}] -> [{}].", message.toString(), e.getMessage());
            messagePayload = RejectMessagePayload.Builder.builder()
                    .setRejectCode(RejectCodeType.REJECT_INVALID)
                    .setMessage(e.getMessage())
                    .setData(message.getChecksum())
                    .build();
            returnMessage = Message.Builder.builder()
                    .setNetwork(message.getNetwork())
                    .setRequestType(CommandType.REJECT_PAYLOAD)
                    .setMessagePayload(messagePayload)
                    .setChecksum(CryptoHasher.hash(messagePayload))
                    .build();
        }

        if(returnMessage != null) {
            try {
                // write the return message back to the nodeconnection
                this.validator.isValidMessage(returnMessage);
                logger.info("Sending message with checksum: {}", CryptoHasher.humanReadableHash(returnMessage.getChecksum()));
                this.sendTargetMessage(returnMessage, nodeConnection);
                this.messagePool.putMessage(returnMessage, nodeConnection);
            } catch (IOException | NoSuchAlgorithmException e) {
                logger.error("Error while sending response message: {}.", e.getMessage());
            }
        }
    }

}
