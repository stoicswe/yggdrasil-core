package org.yggdrasil.node.network.runners;

import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.exceptions.HandshakeInitializeException;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.NetworkType;
import org.yggdrasil.node.network.messages.enums.RequestType;
import org.yggdrasil.node.network.messages.payloads.AcknowledgeMessage;
import org.yggdrasil.node.network.messages.payloads.HandshakeMessage;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * The handshake runner is used to initialize the handshake
 * between two nodes. This must happen before data transfer
 * is fully enabled.
 *
 * @since 0.0.14
 * @author nathanielbunch
 */
public class HandshakeRunner implements Runnable {

    Logger logger = LoggerFactory.getLogger(HandshakeRunner.class);

    Node node;
    NodeConfig nodeConfig;
    Messenger messenger;
    NodeConnection nodeConnection;
    boolean initializeHandShake;

    public HandshakeRunner(Node node, NodeConfig nodeConfig, Messenger messenger, NodeConnection nodeConnection, boolean initializeHandshake) {
        this.node = node;
        this.nodeConfig = nodeConfig;
        this.messenger = messenger;
        this.nodeConnection = nodeConnection;
        this.initializeHandShake = initializeHandshake;
    }

    @Override
    public void run() {
        try {
            Message receivedMessage;
            Message sentMessage;
            if(initializeHandShake && nodeConnection.isConnected()) {
                logger.info("Initializing the handshake with [{}]", this.nodeConnection.getNodeSocket().getInetAddress());
                // build the handshake
                int sendingTime = (int) DateTimeUtil.getCurrentTimestamp().toEpochSecond();
                HandshakeMessage offerHandshake = HandshakeMessage.Builder.newBuilder()
                        .setVersion(1)
                        .setTimestamp(sendingTime)
                        .setServices(null)
                        .setSenderIdentifier(this.nodeConfig.getNodeIdentifier().toCharArray())
                        .setSenderAddress(this.nodeConfig.getNodeIp().toString().toCharArray())
                        .setSenderPort(this.nodeConfig.getPort())
                        .setReceiverAddress(this.nodeConnection.getNodeSocket().getInetAddress().toString().toCharArray())
                        .setReceiverPort(this.nodeConnection.getNodeSocket().getPort())
                        .build();
                // build the message
                sentMessage = Message.Builder.newBuilder()
                        .setNetwork(NetworkType.getByValue(nodeConfig.getNetwork().toUpperCase(Locale.ROOT).toCharArray()))
                        .setRequestType(RequestType.HANDSHAKE_OFFR)
                        .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(offerHandshake).totalSize()))
                        .setMessagePayload(offerHandshake)
                        .setChecksum(CryptoHasher.hash(offerHandshake))
                        .build();
                logger.info("Offering handshake to [{}]", this.nodeConnection.getNodeSocket().getInetAddress());
                // send the message containing the handshake payload
                this.messenger.sendTargetMessage(this.nodeConnection, sentMessage);
                // wait for the response
                logger.info("Waiting for handshake response from [{}]", this.nodeConnection.getNodeSocket().getInetAddress());
                while((receivedMessage = (Message) this.nodeConnection.getNodeInput().readObject()) != null) {
                    logger.info("Received a response from [{}]", this.nodeConnection.getNodeSocket().getInetAddress());
                    // validate the incoming message
                    messenger.getValidator().isValidMessage(receivedMessage);
                    // complete the handshake
                    logger.info("Verifying handshake response from [{}]", this.nodeConnection.getNodeSocket().getInetAddress());
                    if(RequestType.HANDSHAKE_RESP.isEqualToLiteral(receivedMessage.getRequest())){
                        // verify the handshake
                        HandshakeMessage rhm = (HandshakeMessage) receivedMessage.getPayload();
                        // Check is correct supported version
                        if((rhm.getVersion() == nodeConfig.getProtocolVersion()) &&
                                // if the time between the send and the send of the other node
                                // is less than 1 minute, proceed to validate the handshake
                                ((rhm.getTimestamp() - sendingTime) < 60000) &&
                                // verify the addresses of the reference to the current node
                                (this.nodeConfig.getNodeIp().toString().contentEquals(String.valueOf(rhm.getReceiverAddress()))) &&
                                (this.nodeConfig.getPort() == rhm.getReceiverPort()) &&
                                // verify that this node is referencing the other node proper
                                (nodeConnection.getNodeSocket().getInetAddress().toString().contentEquals(String.valueOf(rhm.getSenderAddress()))) &&
                                (nodeConnection.getNodeSocket().getPort() == rhm.getSenderPort())) {
                            // set the supported services
                            nodeConnection.setSupportedServices(rhm.getServices());
                            // set the identifier
                            nodeConnection.setNodeIdentifier(String.valueOf(rhm.getSenderIdentifier()));
                            // add the node connection to the pool
                            logger.info("Handshake validated from [{}], identified as: '{}'", this.nodeConnection.getNodeSocket().getInetAddress(), nodeConnection.getNodeIdentifier());
                            this.node.getConnectedNodes().put(nodeConnection.getNodeIdentifier(), nodeConnection);
                            // make and send acknowledgement message
                            logger.info("Build an acknowledgement to send to {}", nodeConnection.getNodeIdentifier());
                            AcknowledgeMessage ackPayload = AcknowledgeMessage.Builder.newBuilder()
                                    .setAcknowledgeChecksum(receivedMessage.getChecksum())
                                    .build();
                            Message ackMessage = Message.Builder.newBuilder()
                                    .setNetwork(NetworkType.getByValue(receivedMessage.getNetwork()))
                                    .setRequestType(RequestType.ACKNOWLEDGE)
                                    .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(ackPayload).totalSize()))
                                    .setMessagePayload(ackPayload)
                                    .setChecksum(CryptoHasher.hash(ackPayload))
                                    .build();
                            this.messenger.sendTargetMessage(nodeConnection, ackMessage);
                            logger.info("Handshake response acknowledgement sent to {}", this.nodeConnection.getNodeIdentifier());
                            // make the connection live
                            logger.info("Connection with {} going live.", this.nodeConnection.getNodeIdentifier());
                            new Thread(nodeConnection).start();
                        } else {
                            throw new HandshakeInitializeException("Handshake failed. Handshake failed evaluation.");
                        }
                    } else {
                        throw new HandshakeInitializeException("Handshake failed. Peer responded with wrong message type.");
                    }
                }
            } else if(nodeConnection.isConnected()) {
                while ((receivedMessage = (Message) this.nodeConnection.getNodeInput().readObject()) != null) {
                    logger.info("Received a message from [{}]", this.nodeConnection.getNodeSocket().getInetAddress());
                    // validate the incoming message
                    messenger.getValidator().isValidMessage(receivedMessage);
                    // complete the handshake
                    logger.info("Verifying handshake offer from [{}]", this.nodeConnection.getNodeSocket().getInetAddress());
                    if(RequestType.HANDSHAKE_OFFR.isEqualToLiteral(receivedMessage.getRequest())){
                        // verify the handshake
                        HandshakeMessage rhm = (HandshakeMessage) receivedMessage.getPayload();
                        // Check is correct supported version
                        if((rhm.getVersion() == nodeConfig.getProtocolVersion()) &&
                                // if the time between the send and the send of the other node
                                // is less than 1 minute, proceed to validate the handshake
                                ((((int) DateTimeUtil.getCurrentTimestamp().toEpochSecond()) - rhm.getTimestamp()) < 60000) &&
                                // verify the addresses of the reference to the current node
                                (this.nodeConfig.getNodeIp().toString().contentEquals(String.valueOf(rhm.getReceiverAddress()))) &&
                                (this.nodeConfig.getPort() == rhm.getReceiverPort()) &&
                                // verify that this node is referencing the other node proper
                                (nodeConnection.getNodeSocket().getInetAddress().toString().contentEquals(String.valueOf(rhm.getSenderAddress()))) &&
                                (nodeConnection.getNodeSocket().getPort() == rhm.getSenderPort())) {
                            // set the supported services
                            nodeConnection.setSupportedServices(rhm.getServices());
                            // set the identifier
                            nodeConnection.setNodeIdentifier(String.valueOf(rhm.getSenderIdentifier()));
                            // add the node connection to the pool
                            logger.info("Handshake offer validated from [{}], identified as: '{}'", this.nodeConnection.getNodeSocket().getInetAddress(), nodeConnection.getNodeIdentifier());
                            // respond to the request for an offer to handshake
                            logger.info("Initializing the handshake response with [{}]", this.nodeConnection.getNodeIdentifier());
                            int sendingTime = (int) DateTimeUtil.getCurrentTimestamp().toEpochSecond();
                            HandshakeMessage offerHandshake = HandshakeMessage.Builder.newBuilder()
                                    .setVersion(1)
                                    .setTimestamp(sendingTime)
                                    .setServices(null)
                                    .setSenderIdentifier(this.nodeConfig.getNodeIdentifier().toCharArray())
                                    .setSenderAddress(this.nodeConfig.getNodeIp().toString().toCharArray())
                                    .setSenderPort(this.nodeConfig.getPort())
                                    .setReceiverAddress(this.nodeConnection.getNodeSocket().getInetAddress().toString().toCharArray())
                                    .setReceiverPort(this.nodeConnection.getNodeSocket().getPort())
                                    .build();
                            // build the message
                            sentMessage = Message.Builder.newBuilder()
                                    .setNetwork(NetworkType.valueOf(nodeConfig.getNetwork().toUpperCase(Locale.ROOT)))
                                    .setRequestType(RequestType.HANDSHAKE_RESP)
                                    .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(offerHandshake).totalSize()))
                                    .setMessagePayload(offerHandshake)
                                    .setChecksum(CryptoHasher.hash(offerHandshake))
                                    .build();
                            logger.info("Sending handshake response to [{}]", this.nodeConnection.getNodeIdentifier());
                            // send the message containing the handshake payload
                            this.messenger.sendTargetMessage(this.nodeConnection, sentMessage);
                            // wait for an acknowledgement
                            logger.info("Waiting for handshake acknowledgement from [{}]", this.nodeConnection.getNodeIdentifier());
                            while ((receivedMessage = (Message) this.nodeConnection.getNodeInput().readObject()) != null) {
                                logger.info("Received message from {}", this.nodeConnection.getNodeIdentifier());
                                // validate the message
                                messenger.getValidator().isValidMessage(receivedMessage);
                                logger.info("Verifying acknowledgement from [{}]", this.nodeConnection.getNodeIdentifier());
                                if(RequestType.ACKNOWLEDGE.isEqualToLiteral(receivedMessage.getRequest())){
                                    // get the acknowledgment
                                    AcknowledgeMessage rackm = (AcknowledgeMessage) receivedMessage.getPayload();
                                    // verify the acknowledgement was for the correct message
                                    if(CryptoHasher.humanReadableHash(sentMessage.getChecksum()).contentEquals(CryptoHasher.humanReadableHash(rackm.getAcknowledgeChecksum()))){
                                        this.node.getConnectedNodes().put(nodeConnection.getNodeIdentifier(), nodeConnection);
                                        // make the connection live
                                        logger.info("Connection with {} going live.", this.nodeConnection.getNodeIdentifier());
                                        new Thread(nodeConnection).start();
                                    } else {
                                        throw new HandshakeInitializeException("Handshake failed. Peer failed to acknowledge handshake response.");
                                    }
                                } else {
                                    throw new HandshakeInitializeException("Handshake failed. Peer responded to handshake response with wrong message type.");
                                }
                            }
                        } else {
                            throw new HandshakeInitializeException("Handshake failed. Handshake failed evaluation.");
                        }
                    } else {
                        throw new HandshakeInitializeException("Handshake failed. Peer responded with wrong message type.");
                    }
                }
            } else {
                throw new HandshakeInitializeException("Handshake failed. Peer was disconnected.");
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Socket input stream read failed with exception: {}", e.getMessage());
            try {
                nodeConnection.getNodeSocket().close();
            } catch (IOException ioException) { }
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to hash handshake offer message payload.");
            try {
                nodeConnection.getNodeSocket().close();
            } catch (IOException ioException) { }
        } catch (HandshakeInitializeException | InvalidMessageException e) {
            logger.error("Handshake failed: {}", e.getLocalizedMessage());
            try {
                nodeConnection.getNodeSocket().close();
            } catch (IOException ioException) { }
        }
    }
}
