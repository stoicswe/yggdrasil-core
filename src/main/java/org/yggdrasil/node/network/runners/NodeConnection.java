package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.Messenger;

import java.io.*;
import java.net.Socket;

/**
 * The node connection is a thread that reacts to incoming messages.
 *
 * @since 0.0.8
 * @author nathanielbunch
 */
public class NodeConnection implements Runnable {

    Logger logger = LoggerFactory.getLogger(NodeConnection.class);

    private String nodeIdentifier;
    private Messenger messenger;
    private Socket nodeSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public NodeConnection(Socket node, Messenger messenger) throws IOException {
        this.nodeSocket = node;
        this.messenger = messenger;
        this.objectOutputStream = new ObjectOutputStream(node.getOutputStream());
        this.objectInputStream = new ObjectInputStream(node.getInputStream());
    }

    protected void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }

    public Socket getNodeSocket() {
        return this.nodeSocket;
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

    public boolean isConnected() {
        return this.nodeSocket.isConnected();
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
                    Message rm = this.messenger.handleMessage(m);
                    // Write the return message
                    this.objectOutputStream.writeObject(rm);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Socket input stream read failed with exception: {}", e.getLocalizedMessage());
                break;
            }
        }
    }
}
