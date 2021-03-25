package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.Messenger;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * The node connection is a thread that reacts to incoming messages.
 *
 * @since 0.0.8
 * @author nathanielbunch
 */
public class NodeConnection implements Runnable {

    Logger logger = LoggerFactory.getLogger(NodeConnection.class);

    @Autowired
    private Messenger messenger;
    private Socket nodeSocket;

    public NodeConnection(Socket node){
        this.nodeSocket = node;
    }

    public Socket getNodeSocket() {
        return this.nodeSocket;
    }

    @Override
    public void run() {
        while(nodeSocket.isConnected()){
            try (InputStream ms = nodeSocket.getInputStream()) {
                try (ObjectInputStream os = new ObjectInputStream(ms)) {
                    Message m = (Message) os.readObject();
                    this.messenger.handleMessage(m);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Socket input stream read failed with exception: {}", e.getLocalizedMessage());
            }
        }
    }
}
