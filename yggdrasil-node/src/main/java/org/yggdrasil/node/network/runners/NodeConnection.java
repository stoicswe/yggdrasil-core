package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.ServicesType;
import org.yggdrasil.node.network.peer.PeerRecord;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * The node connection is a thread that reacts to incoming messages.
 *
 * @since 0.0.8
 * @author nathanielbunch
 */
public class NodeConnection implements Runnable {

    Logger logger = LoggerFactory.getLogger(NodeConnection.class);

    private final Messenger messenger;
    private final Socket nodeSocket;
    private int port;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;
    private String nodeIdentifier;
    private ServicesType supportedServices;

    public NodeConnection(Socket node, Messenger messenger) throws IOException {
        this.nodeSocket = node;
        this.messenger = messenger;
        this.objectOutputStream = new ObjectOutputStream(node.getOutputStream());
        this.objectInputStream = new ObjectInputStream(node.getInputStream());
    }

    protected void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }

    protected void setSupportedServices(ServicesType supportedServices) {
        this.supportedServices = supportedServices;
    }

    public Socket getNodeSocket() {
        return this.nodeSocket;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    public ObjectInputStream getNodeInput() {
        return this.objectInputStream;
    }

    public ObjectOutputStream getNodeOutput() {
        return this.objectOutputStream;
    }

    public ServicesType getSupportedServices() {
        return this.supportedServices;
    }

    public boolean isConnected() {
        return this.nodeSocket.isConnected();
    }

    public PeerRecord toPeerRecord() {
        return PeerRecord.Builder.newBuilder()
                .setNodeIdentifier(this.nodeIdentifier)
                .setTimeStamp(DateTimeUtil.getCurrentTimestamp())
                .setSupportedServices(this.supportedServices)
                .setIpAddress(this.nodeSocket.getInetAddress().getHostAddress())
                .setPort(this.port)
                .build();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == null || !(obj instanceof NodeConnection)){
            return false;
        }

        NodeConnection n = (NodeConnection) obj;
        return n.nodeSocket.getInetAddress().equals(this.nodeSocket.getInetAddress());
    }

    @Override
    public void run() {
        while(nodeSocket.isConnected()){
            // Handle incoming message
            try {
                Message m;
                while((m = (Message) this.objectInputStream.readObject()) != null) {
                    logger.info("Received message: {} from: {}", m.toString(), this.getNodeIdentifier());
                    this.messenger.handleMessage(m, this);
                }
            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
                logger.error("Socket input stream read failed with exception: {}", e.getLocalizedMessage());
                break;
            }
        }
    }
}
