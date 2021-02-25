package org.nathanielbunch.ssblockchain.core.utils;

import org.apache.commons.lang3.SerializationUtils;
import org.nathanielbunch.ssblockchain.core.ledger.Block;
import org.nathanielbunch.ssblockchain.core.ledger.Transaction;
import org.nathanielbunch.ssblockchain.core.ledger.Wallet;

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
public class BCOHasher {

    private static final String _HASH_ALGORITHM = "SHA3-512";

    /**
     * Hashes a SSBlock.
     *
     * @param block
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(Block block) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(block));
    }

    /**
     * Hashes a SSTransaction.
     *
     * @param transaction
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(Transaction transaction) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(transaction));
    }

    /**
     * Hashes a SSWallet.
     *
     * @param wallet
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hash(Wallet wallet) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(_HASH_ALGORITHM).digest(SerializationUtils.serialize(wallet));
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

}
