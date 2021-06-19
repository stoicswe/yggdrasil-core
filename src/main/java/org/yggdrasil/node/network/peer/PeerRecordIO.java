package org.yggdrasil.node.network.peer;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PeerRecordIO {

    private final Logger logger = LoggerFactory.getLogger(PeerRecordIO.class);

    private static final String _HASH_ALGORITHM = "MD5";
    private final String _CURRENT_DIRECTORY = System.getProperty("user.dir") + "/.blockchain-data/peers";
    private final String _FILE_EXTENSION = ".0x";

    public void dumpPeerRecords(List<PeerRecord> peerRecords) throws NoSuchAlgorithmException, IOException {
        for(PeerRecord pr : peerRecords) {
            writePeerRecord(pr);
        }
    }

    public void writePeerRecord(PeerRecord peerRecord) throws NoSuchAlgorithmException, IOException {
        logger.debug("Writing new peer to storage: {}", peerRecord.getNodeIdentifier().toString());
        try(FileOutputStream currentPeerRecord = new FileOutputStream(new File(_CURRENT_DIRECTORY + "/" + this.humanReadableHash(this.hashPeerRecord(peerRecord)) + _FILE_EXTENSION))) {
            try(ObjectOutputStream currentBlockObject = new ObjectOutputStream(currentPeerRecord)) {
                currentBlockObject.writeObject(peerRecord);
            }
        }
        logger.debug("Peer record written successfully.");
    }

    private byte[] hashPeerRecord(PeerRecord peerRecord) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(peerRecord));
    }

    private String humanReadableHash(byte[] hash){
        return HexUtils.toHexString(hash);
    }

}
