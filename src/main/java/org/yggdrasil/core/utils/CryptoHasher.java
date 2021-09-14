package org.yggdrasil.core.utils;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.apache.tomcat.util.buf.HexUtils;
import org.yggdrasil.core.ledger.LedgerHashableItem;
import org.yggdrasil.node.network.messages.MessagePayload;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * The CryptoHasher provides useful tooling for hashing transactions and blocks using
 * a specified hashing algorithm. It also provides the ability to print hashes in
 * a human-readable hex format. Blocks and Transactions are handled separately
 * in case in the future there is the desire to hash either one with different
 * algorithms.
 *
 * @since 0.0.3
 * @author nathanielbunch
 */
public class CryptoHasher {

    private static final String _HASH_ALGORITHM = "SHA-256";

    /**
     * Function for hashing items that support hashing (txns, blocks, etc).
     *
     * @param item
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(LedgerHashableItem item) throws NoSuchAlgorithmException {
        return CryptoHasher.dhash(item.getDataBytes());
    }

    /**
     * Hashes a given message payload twice.
     *
     * @param payload
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(MessagePayload payload) throws NoSuchAlgorithmException {
        return CryptoHasher.dhash(payload.getDataBytes());
    }

    /**
     * Hashes an arbitrary object twice.
     *
     *
     */
    public static byte[] dhash(byte[] object) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM)
                .digest(MessageDigest.getInstance(_HASH_ALGORITHM)
                        .digest(object));
    }

    public static byte[] shash(byte[] object) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM)
                .digest(object);
    }

    /**
     * Hashes a wallet address.
     */
    public static byte[] generateWalletAddress(PublicKey publicKey) throws NoSuchAlgorithmException {
        byte[] encodedPk = CryptoHasher.shash(publicKey.getEncoded());
        // seems to always be 20 bits from some experimenting
        byte[] address = new byte[20];
        RIPEMD160Digest rpmd160 = new RIPEMD160Digest();
        rpmd160.update(encodedPk, 0, encodedPk.length);
        rpmd160.doFinal(address, 0);
        return address;
    }

    /**
     * Returns a human-readable hex string of a given hash.
     *
     * @param hash
     * @return
     */
    public static String humanReadableHash(byte[] hash){
        return HexUtils.toHexString(hash);
    }

    /**
     * Returns a hash imported from a human-readable hex string.
     *
     * @param stringHash
     * @return
     */
    public static byte[] hashByteArray(String stringHash){
        return HexUtils.fromHexString(stringHash);
    }

    /**
     * Compare two hashes for equality.
     *
     * @param val0
     * @param val1
     * @return
     */
    public static boolean isEqualHashes(byte[] val0, byte[] val1) {
        try {
            for (int i = 0; i < val1.length; i++) {
                if (val0[i] != val1[i]) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Compare two hashes.
     *
     * @param val0
     * @param val1
     * @return
     */
    public static int compareHashes(byte[] val0, byte[] val1) {

        if (val0 == val1) {
            return 0;
        } else if (val0 == null) {
            return -1; // "a < b"
        } else if (val1 == null) {
            return 1; // "a > b"
        }

        int last = Math.min(val0.length, val1.length);
        for (int i = 0; i < last; i++) {
            byte val0b = val0[i];
            byte val1b = val1[i];
            if (val0b != val1b) {
                if (val0b < val1b) {
                    return -1;
                }
                return 1;
            }
        }
        return 0;
    }

}
