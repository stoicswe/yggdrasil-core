package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.Messenger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

/**
 * The node connection is a thread that reacts to incoming messages.
 *
 * @since 0.0.8
 * @author nathanielbunch
 */
public class NodeConnection implements Runnable {

    Logger logger = LoggerFactory.getLogger(NodeConnection.class);

    private Messenger messenger;
    private Socket nodeSocket;
    //private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    //private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;

    public NodeConnection(Socket node, Messenger messenger) throws IOException {
        this.nodeSocket = node;
        this.messenger = messenger;
        this.objectInputStream = new ObjectInputStream(node.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(node.getOutputStream());
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
                Message m = (Message) this.objectInputStream.readObject();
                Message rm = this.messenger.handleMessage(m);
                // Write the return message
                this.objectOutputStream.writeObject(rm);
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Socket input stream read failed with exception: {}", e.getLocalizedMessage());
                break;
            }
        }
    }
}
