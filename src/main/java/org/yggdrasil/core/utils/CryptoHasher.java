package org.yggdrasil.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.Wallet;
import org.yggdrasil.node.network.messages.MessagePayload;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The SSHasher provides useful tooling for hashing transactions and blocks using
 * a specified hashing algorithm. It also provides the ability to print hashes in
 * a human-readable hex format. SSBlocks and SSTransactions are handled separately
 * in case in the future there is the desire to hash either one with different
 * algorithms.
 *
 * @since 0.0.3
 * @author nathanielbunch
 */
public class CryptoHasher {

    private static final String _HASH_ALGORITHM = "SHA-256";

    /**
     * Hashes a Block.
     *
     * @param block
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(Block block) throws NoSuchAlgorithmException {
        byte[] blockData = new byte[0];
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getTimestamp()));
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getData().hashCode()));
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getPreviousBlockHash()));
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getNonce()));
        blockData = appendBytes(blockData, block.getValidator());
        blockData = appendBytes(blockData, block.getSignature());
        return CryptoHasher.dhash(blockData);
    }

    /**
     * Hashes a Transaction.
     *
     * @param transaction
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(Transaction transaction) throws NoSuchAlgorithmException {
        byte[] txnData = new byte[0];
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getTimestamp()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getOrigin()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getDestination()));
        txnData = appendBytes(txnData, transaction.getSignature());
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getTxnInputs()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getTxnOutPuts()));
        return CryptoHasher.dhash(txnData);
    }

    /**
     * Hashes a Wallet.
     *
     * @param wallet
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(Wallet wallet) throws NoSuchAlgorithmException {
        byte[] walletData = new byte[0];
        walletData = appendBytes(walletData, SerializationUtils.serialize(wallet.getAddress()));
        walletData = appendBytes(walletData, SerializationUtils.serialize(wallet.getCreationDate()));
        return CryptoHasher.dhash(walletData);
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

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

}
