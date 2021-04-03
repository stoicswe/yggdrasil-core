package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Messenger messenger;
    private Socket nodeSocket;

    public NodeConnection(Socket node, Messenger messenger){
        this.nodeSocket = node;
        this.messenger = messenger;
    }

    public Socket getNodeSocket() {
        return this.nodeSocket;
    }

    @Override
    public void run() {
        while(nodeSocket.isConnected()){
            // Handle incoming message
            try {
                InputStream mis = nodeSocket.getInputStream();
                try (ObjectInputStream ois = new ObjectInputStream(mis)) {
                    Message m = (Message) ois.readObject();
                    Message rm = this.messenger.handleMessage(m);
                    // Write the return message
                    try (OutputStream mos = nodeSocket.getOutputStream()) {
                        try (ObjectOutputStream oos = new ObjectOutputStream(mos)) {
                            oos.writeObject(rm);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Socket input stream read failed with exception: {}", e.getLocalizedMessage());
                break;
            }
        }
    }
}
