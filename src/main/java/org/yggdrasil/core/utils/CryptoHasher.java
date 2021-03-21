package org.yggdrasil.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.transaction.Txn;
import org.yggdrasil.core.ledger.Wallet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The SSHasher provides useful tooling for hashing transactions and blocks using
 * a specified hashing algorithm. It also provides the ability to print hashes in
 * a human-readable hex format. SSBlocks and SSTransactions are handled separately
 * in case in the future there is the desire to hash either one with different
 * algorithms.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public class CryptoHasher {

    private static final String _HASH_ALGORITHM = "SHA-256";

    /**
     * Hashes a SSBlock.
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
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(blockData));
    }

    /**
     * Hashes a SSTransaction.
     *
     * @param txn
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(Txn txn) throws NoSuchAlgorithmException {
        byte[] txnData = new byte[0];
        txnData = appendBytes(txnData, SerializationUtils.serialize(txn.getIndex()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(txn.getTimestamp()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(txn.getOrigin()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(txn.getDestination()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(txn.getAmount()));
        txnData = appendBytes(txnData, SerializationUtils.serialize(txn.getNote()));
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(txnData));
    }

    /**
     * Hashes a SSWallet.
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
     * Returns a human-readable hex string of a given hash.
     *
     * @param hash
     * @return
     */
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

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

}
