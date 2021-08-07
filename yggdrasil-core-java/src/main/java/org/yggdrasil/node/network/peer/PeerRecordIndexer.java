package org.yggdrasil.node.network.peer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.runners.PeerRecordKeeperRunner;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

@Component
public class PeerRecordIndexer {

    private Logger logger = LoggerFactory.getLogger(PeerRecordIndexer.class);

    @Autowired
    private PeerRecordIO peerRecordIO;
    @Autowired
    private NodeConfig nodeConfig;
    private List<PeerRecord> peerRecords;
    private Timer recordKeeperTimer;
    private transient final Object lock = new Object();

    @PostConstruct
    private void init() {
        this.peerRecords = new ArrayList<>();
        this.recordKeeperTimer = new Timer();
        this.recordKeeperTimer.schedule(new PeerRecordKeeperRunner(this), 60000, 60000);
    }

    public int getPeerRecordCount() {
        return this.peerRecords.size();
    }

    public List<PeerRecord> getPeerRecords() {
        return this.peerRecords;
    }

    public List<PeerRecord> getPeerRecords(int count) {
        synchronized (lock) {
            List<PeerRecord> returnList = new ArrayList<>();
            if (count > this.peerRecords.size()) {
                count = peerRecords.size();
            }
            for (int i = 0; i < count; i++) {
                returnList.add(peerRecords.get(i));
            }
            return returnList;
        }
    }

    public void addPeerRecord(PeerRecord peerRecord) {
        if (this.nodeConfig.getNodeIndex().compareTo(peerRecord.getNodeIdentifier()) != 0) {
            logger.debug("Adding new peer record, {}", peerRecord.getNodeIdentifier());
            if (this.containsPeerRecord(peerRecord.getIpAddress())) {
                PeerRecord oldPeerRecord = peerRecords.stream().filter(pr -> pr.getIpAddress().contentEquals(peerRecord.getIpAddress())).findFirst().get();
                if (oldPeerRecord.getTimeStamp().isBefore(peerRecord.getTimeStamp())) {
                    synchronized (lock) {
                        logger.debug("Old record with the same name, removing, {}", peerRecord.getNodeIdentifier());
                        peerRecords.remove(oldPeerRecord);
                    }
                }
            }
            peerRecords.add(peerRecord);
            logger.debug("Added peer record: {}", peerRecord.getNodeIdentifier());
        }
    }

    public boolean containsPeerRecord(String ipAddress) {
        return (peerRecords.stream().anyMatch(peerRecord -> peerRecord.getIpAddress().contentEquals(ipAddress)));
    }

    public boolean containsPeerRecord(UUID nodeIdentifier) {
        return (peerRecords.stream().anyMatch(peerRecord -> peerRecord.getNodeIdentifier().compareTo(nodeIdentifier) == 0));
    }

    public void loadPeerRecords(int numberOfRecords) throws IOException, ClassNotFoundException {
        logger.debug("Attempting to load peer records.");
        String[] peerRecords = peerRecordIO.getPeerRecords();
        logger.debug("There are {} peer records found in storage.", peerRecords.length);
        if(numberOfRecords > peerRecords.length){
            numberOfRecords = peerRecords.length;
        }
        logger.debug("Loading {} peer records.", numberOfRecords);
        for(int i = 0; i < numberOfRecords; i++){
            PeerRecord pr = null;
            try{
                pr = this.peerRecordIO.readPeerRecord(peerRecords[i]);
            } catch (Exception e){}
            if (pr != null) {
                this.addPeerRecord(pr);
            }
        }
    }

    public void dumpPeerRecords() throws NoSuchAlgorithmException, IOException {
        logger.debug("Asked to dump peer records, complying.");
        this.peerRecordIO.dumpPeerRecords(this.peerRecords);
    }

}
