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
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getIndex()));
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getTimestamp()));
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getData().hashCode()));
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getPreviousBlockHash()));
        blockData = appendBytes(blockData, SerializationUtils.serialize(block.getNonce()));
        blockData = appendBytes(blockData, block.getValidator());
        blockData = appendBytes(blockData, block.getSignature());
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(blockData));
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
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getIndex()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getTimestamp()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getOrigin()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getDestination()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getValue()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getNote()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(transaction.getSignature()));
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(txnData));
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
        walletData = appendBytes(walletData, SerializationUtils.serialize(wallet.getIndex()));
        walletData = appendBytes(walletData, SerializationUtils.serialize(wallet.getAddress()));
        walletData = appendBytes(walletData, SerializationUtils.serialize(wallet.getCreationDate()));
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(walletData));
    }

    /**
     * Hashes a given message payload twice.
     *
     * @param payload
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(MessagePayload payload) throws  NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM)
                .digest(MessageDigest.getInstance(_HASH_ALGORITHM)
                        .digest(SerializationUtils.serialize(payload.getDataBytes())));
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

    public static boolean compareHashes(byte[] val0, byte[] val1) {
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

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

}
