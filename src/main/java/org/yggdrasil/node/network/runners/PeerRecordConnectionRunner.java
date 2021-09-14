package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.peer.PeerRecord;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;

import java.net.Socket;
import java.util.TimerTask;

public class PeerRecordConnectionRunner extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(PeerRecordConnectionRunner.class);
    private Node node;
    private NodeConfig nodeConfig;
    private Messenger messenger;
    private PeerRecordIndexer peerRecordIndexer;

    public PeerRecordConnectionRunner(Node node, NodeConfig nodeConfig, Messenger messenger, PeerRecordIndexer peerRecordIndexer) {
        this.node = node;
        this.nodeConfig = nodeConfig;
        this.messenger = messenger;
        this.peerRecordIndexer = peerRecordIndexer;
    }

    @Override
    public void run() {
        try {
            if(node.getConnectedNodes().size() < nodeConfig.getActiveConnections()) {
                logger.trace("Current connected peers < peer limit, checking peer records.");
                for(PeerRecord pr : peerRecordIndexer.getPeerRecords()) {
                    if(node.getConnectedNodes().values().stream().noneMatch(nodeConnection -> nodeConnection.getNodeSocket().getInetAddress().getHostAddress().contentEquals(pr.getIpAddress()))){
                        logger.debug("Peer is not connected to yet. Attempting to handshake.");
                        Socket peer = new Socket(pr.getIpAddress(), pr.getPort());
                        new Thread(new HandshakeRunner(this.node, this.nodeConfig, this.messenger, new NodeConnection(peer, this.messenger), this.peerRecordIndexer, true)).start();
                        logger.debug("Sleeping for a second, giving time for handshake to complete.");
                        Thread.sleep(5000);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("There was an error while trying to connect to peers in peer records.");
        }
    }

}
