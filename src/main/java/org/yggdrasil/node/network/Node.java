package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.peer.PeerRecord;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;
import org.yggdrasil.node.network.runners.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @Autowired
    private PeerRecordIndexer peerRecordIndexer;
    private ServerSocket serverSocket;
    private NodeConnectionHashMap<String, NodeConnection> connectedNodes;

    @PostConstruct
    public void init() throws IOException, ClassNotFoundException {
        this.connectedNodes = new NodeConnectionHashMap<>(nodeConfig.getActiveConnections());
        this.serverSocket = new ServerSocket(nodeConfig.getPort(), 3, nodeConfig.getNodeIp());
        logger.info("P2P Server listening on {}:{}", nodeConfig.getNodeIp(), nodeConfig.getPort());
        new Thread(new PeerRecordConnectionRunner(this.nodeConfig, this.peerRecordIndexer)).start();
        new Thread(new PeerConnectionRunner(this)).start();
        new Thread(new NodeRunner(this)).start();
    }

    public NodeConnectionHashMap<String, NodeConnection> getConnectedNodes() {
        return this.connectedNodes;
    }

    public void establishConnections() throws IOException {
        logger.info("Reading pre-configured peers");
        for (String ipString : nodeConfig.getPeers()) {
            logger.info("Attempting to connect to peer: {}", ipString);
            try {
                Socket peer = new Socket(ipString, nodeConfig.getPort());
                peer.setKeepAlive(true);
                if (!peer.getInetAddress().equals(nodeConfig.getNodeIp())) {
                    new Thread(new HandshakeRunner(this, this.nodeConfig, this.messenger, new NodeConnection(peer, this.messenger), this.peerRecordIndexer, true)).start();
                } else {
                    peer.close();
                }
            } catch (IOException ie) {
                logger.info("Failed to connect to peer: {}. Are you sure you are online?", ipString);
            }
        }
    }

    public void startListening() throws IOException, ClassNotFoundException {
        Socket peer;
        //int cliNum = 0;
        while(true){
            logger.info("Ready for connections.");
            peer = serverSocket.accept();
            if(peer.getInetAddress() != nodeConfig.getNodeIp()) {
                logger.info("Accepted new connection from: [{}].", peer.getInetAddress());
                peer.setKeepAlive(true);
                //client.setSoTimeout(nodeConfig.getTimeout());
                if (connectedNodes.size() < nodeConfig.getActiveConnections()) {
                    try {
                        logger.info("Attempting handshake with: [{}]", peer.getInetAddress());
                        new Thread(new HandshakeRunner(this, this.nodeConfig, this.messenger, new NodeConnection(peer, this.messenger), this.peerRecordIndexer, false)).start();
                    } catch (Exception e) {
                        logger.error("Error while attempting handshake: {}", e.getMessage());
                        peer.close();
                    }
                } else {
                    logger.debug("Maximum connections have been reached.");
                    peer.close();
                }
                logger.info("Number of connected nodes: {}", connectedNodes.size());
            } else {
                logger.info("Tried to connect to self.");
                peer.close();
            }
        }
    }
}
