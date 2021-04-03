package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.Messenger;

import javax.annotation.PostConstruct;
import java.io.IOException;
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
        logger.info("P2P Connect listening on {}:{}", nodeConfig.getNodeIp(), nodeConfig.getPort());
        new Thread(new NodeRunner(this)).start();
        this.establishConnections();
    }

    public HashMap<String, NodeConnection> getConnectedNodes() {
        return this.connectedNodes;
    }

    public void establishConnections() throws IOException {
        int peerNum = 0;
        for (String ipString : nodeConfig.getPeers()) {
            logger.info("Attempting to connect to peer: {}", ipString);
            Socket s = new Socket(ipString, nodeConfig.getPort());
            s.setKeepAlive(true);
            //s.setSoTimeout(nodeConfig.getTimeout());
            NodeConnection n = new NodeConnection(s, this.messenger);
            boolean isAlreadyConnected = false;
            for (NodeConnection nc : this.connectedNodes.values()) {
                if (n.equals(nc)) {
                    isAlreadyConnected = true;
                }
            }
            if (!isAlreadyConnected) {
                this.connectedNodes.put("peer-" + peerNum, n);
                peerNum++;
            } else {
                s.close();
            }
        }
    }

    public void startListening() throws IOException, ClassNotFoundException {
        Socket client;
        int cliNum = 0;
        while(true){
            logger.info("Ready for connections.");
            client = serverSocket.accept();
            logger.info("Accepted new connection from: [{}].", client.getInetAddress());
            client.setKeepAlive(true);
            //client.setSoTimeout(nodeConfig.getTimeout());
            if(connectedNodes.size() < nodeConfig.getActiveConnections()) {
                try {
                    connectedNodes.put("peer-"+cliNum, new NodeConnection(client, this.messenger));
                    new Thread(connectedNodes.get("peer-"+cliNum)).start();
                    logger.info("Added peer: {}", "peer-"+cliNum);
                    cliNum++;
                } catch (Exception e) {
                    logger.error("Error while attempting to open connection: {}", e.getMessage());
                    client.close();
                }
            } else {
                logger.error("Maximum connections have been reached.");
                client.close();
            }
            logger.info("Number of connected nodes: {}", connectedNodes.size());
        }
    }
}
