package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;

public class PeerRecordStartupRunner implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PeerRecordStartupRunner.class);
    private NodeConfig nodeConfig;
    private PeerRecordIndexer peerRecordIndexer;

    public PeerRecordStartupRunner(NodeConfig nodeConfig, PeerRecordIndexer peerRecordIndexer){
        this.nodeConfig = nodeConfig;
        this.peerRecordIndexer = peerRecordIndexer;
    }

    @Override
    public void run() {
        try {
            logger.info("Reading peer connection files into memory.");
            this.peerRecordIndexer.loadPeerRecords(nodeConfig.getActiveConnections());
        } catch (Exception e) {
            logger.info("No peer records available.");
        }
    }

}
