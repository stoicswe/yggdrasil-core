package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.peer.PeerRecordIndexer;

import java.util.TimerTask;

public class PeerRecordKeeperRunner extends TimerTask {

    private Logger logger = LoggerFactory.getLogger(PeerRecordKeeperRunner.class);

    private PeerRecordIndexer peerRecordIndexer;

    public PeerRecordKeeperRunner(PeerRecordIndexer peerRecordIndexer) {
        this.peerRecordIndexer = peerRecordIndexer;
    }

    @Override
    public void run() {
        try{
            logger.trace("Dumping peer records to storage.");
            this.peerRecordIndexer.dumpPeerRecords();
        } catch (Exception e) {
            logger.error("There was an issue dumping peer records: {}", e.getMessage());
        }
    }

}
