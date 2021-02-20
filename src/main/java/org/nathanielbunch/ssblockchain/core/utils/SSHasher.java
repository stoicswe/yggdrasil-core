package org.nathanielbunch.ssblockchain.core.utils;

import org.apache.commons.lang3.SerializationUtils;
import org.nathanielbunch.ssblockchain.core.ledger.SSBlock;
import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SSHasher {

    private static final String _HASH_ALGORITHM = "SHA3-512";

    public static byte[] hash(SSBlock ssBlock) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(ssBlock));
    }

    public static byte[] hash(SSTransaction ssTransaction) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(ssTransaction));
    }

    public static String humanReadableHash(byte[] hash){
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
