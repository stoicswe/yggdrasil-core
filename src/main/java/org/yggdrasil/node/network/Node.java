package org.yggdrasil.node.network;

import org.yggdrasil.node.network.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.Messenger;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The node class handles the binding of the socket, and initial openeing
 * of connections to other nodes. Messages are not handled here.
 *
 * @since 0.0.8
 * @author nathanielbunch
 */
@Profile("!test")
@Component
public class Node {

    private final Logger logger = LoggerFactory.getLogger(Node.class);

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;
    private ServerSocket serverSocket;
    private HashMap<String, NodeConnection> connectedNodes;

    @PostConstruct
    public void init() throws IOException, ClassNotFoundException {
        this.connectedNodes = new HashMap<>();
        this.serverSocket = new ServerSocket(nodeConfig.getPort(), 3, nodeConfig.getNodeIp());
        Thread nodeRunner = new Thread(new NodeRunner(this));
        nodeRunner.start();
    }

    public HashMap<String, NodeConnection> getConnectedNodes() {
        return this.connectedNodes;
    }

    public void startListening() throws IOException, ClassNotFoundException {
        Socket client;
        while(true){
            client = serverSocket.accept();
            logger.debug("Accepted new connection from: [{}].", client.getInetAddress());
            if(nodeConfig.getActiveConnections() < connectedNodes.size()) {
                client.setKeepAlive(true);
                client.setSoTimeout(nodeConfig.getTimeout());
                InputStream bis = client.getInputStream();
                ObjectInputStream objIn = new ObjectInputStream(bis);
                Message m = (Message) objIn.readObject();
                logger.debug("Received message: [{}]", m.toString());
                messenger.handleMessage(m);
            } else {
                client.close();
            }
        }
    }
}
