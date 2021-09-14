package org.yggdrasil.node.network.peer;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.NodeConfig;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

@Component
public class PeerRecordIO {

    private final Logger logger = LoggerFactory.getLogger(PeerRecordIO.class);

    private static final String _HASH_ALGORITHM = "MD5";
    @Autowired
    private NodeConfig nodeConfig;

    private Random random = new Random();

    public void dumpPeerRecords(List<PeerRecord> peerRecords) throws NoSuchAlgorithmException, IOException {
        for(PeerRecord pr : peerRecords) {
            this.writePeerRecord(pr);
        }
    }

    public void writePeerRecord(PeerRecord peerRecord) throws NoSuchAlgorithmException, IOException {
        logger.debug("Writing new peer to storage: {}", peerRecord.getNodeIdentifier().toString());
        String fileName = nodeConfig._PEER_DATA_DIRECTORY + "/" + this.humanReadableHash(this.hashPeerRecord(peerRecord)) + nodeConfig._FILE_EXTENSION;
        File f = new File(fileName);
        if(f.exists()){
            f.delete();
        }
        try(FileOutputStream currentPeerRecord = new FileOutputStream(new File(fileName))) {
            try(ObjectOutputStream currentBlockObject = new ObjectOutputStream(currentPeerRecord)) {
                currentBlockObject.writeObject(peerRecord);
            }
        }
        logger.debug("Peer record written successfully.");
    }

    public PeerRecord readPeerRecord() throws IOException, ClassNotFoundException {
        logger.debug("Reading random peer record from storage...");
        String[] peerFiles = this.getPeerRecords();
        int peerFile = random.nextInt(peerFiles.length);
        FileInputStream currentPeerRecord = new FileInputStream(new File(nodeConfig._PEER_DATA_DIRECTORY + "/" + peerFiles[peerFile] + nodeConfig._FILE_EXTENSION));
        ObjectInputStream currentPeerRecordObj = new ObjectInputStream(currentPeerRecord);
        PeerRecord peerRecord = (PeerRecord) currentPeerRecordObj.readObject();
        logger.debug("Peer read successfully: {}", peerRecord.getNodeIdentifier());
        return peerRecord;
    }

    public PeerRecord readPeerRecord(String peerRecordIndex) throws IOException, ClassNotFoundException {
        logger.debug("Reading random peer record from storage...");
        if(!peerRecordIndex.contains(".0x")){
            peerRecordIndex += ".0x";
        }
        FileInputStream currentPeerRecord = new FileInputStream(new File(nodeConfig._PEER_DATA_DIRECTORY + "/" + peerRecordIndex));
        ObjectInputStream currentPeerRecordObj = new ObjectInputStream(currentPeerRecord);
        PeerRecord peerRecord = (PeerRecord) currentPeerRecordObj.readObject();
        logger.debug("Peer read successfully: {}", peerRecord.getNodeIdentifier());
        return peerRecord;
    }

    public String[] getPeerRecords() {
        File peerSaveDir = new File(nodeConfig._PEER_DATA_DIRECTORY);
        return peerSaveDir.list();
    }

    private byte[] hashPeerRecord(PeerRecord peerRecord) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(peerRecord));
    }

    private String humanReadableHash(byte[] hash){
        return HexUtils.toHexString(hash);
    }

}
