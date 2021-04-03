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
            if(initializeHandShake) {
                // build the handshake
                HandshakeMessage offerHandshake = HandshakeMessage.Builder.newBuilder()
                        .setVersion(1)
                        .setTimestamp((int) DateTimeUtil.getCurrentTimestamp().toEpochSecond())
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
                        .setRequestType(RequestType.HANDSHAKE_OFFR)
                        .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(offerHandshake).totalSize()))
                        .setMessagePayload(offerHandshake)
                        .setChecksum(CryptoHasher.hash(offerHandshake))
                        .build();
                // send the message containing the handshake payload
                this.messenger.sendTargetMessage(this.nodeConnection, sentMessage);
                // wait for the response
                while((receivedMessage = (Message) this.nodeConnection.getNodeInput().readObject()) != null) {

                    // validate the incoming message
                    messenger.getValidator().isValidMessage(receivedMessage);

                    // complete the handshake
                    if(RequestType.HANDSHAKE_RESP.isEqualToLiteral(receivedMessage.getRequest())){
                        // verify the handshake


                    } else {
                        throw new HandshakeInitializeException("Handshake failed. Peer responded with wrong message type.");
                    }
                }
            } else {
                while ((receivedMessage = (Message) this.nodeConnection.getNodeInput().readObject()) != null) {
                    Message rm = this.messenger.handleMessage(receivedMessage);

                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Socket input stream read failed with exception: {}", e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to hash handshake offer message payload.");
        } catch (HandshakeInitializeException | InvalidMessageException e) {
            logger.error("Handshake failed: {}", e.getMessage());
        }
    }
}
