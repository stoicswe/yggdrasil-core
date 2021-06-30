package org.yggdrasil.core.ledger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.transaction.TransactionOutput;
import org.yggdrasil.core.ledger.transaction.WalletTransaction;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.core.utils.DateTimeUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

/**
 * The Wallet will contain the ability to send, receive, and store crypto
 * in an encrypted environment. The Wallet will also be capable of creating
 * an identifiable address in order to send and receive value.
 *
 * @since 0.0.5
 * @author nathanielbunch
 */
public class Wallet implements Serializable {

    /**
     * Make a method of where the public and private keys can
     * be saved for reference later. Somehow make a way so that
     * the public portions of a wallet can be saved separate from
     * the private key, so that the private key is never stored in
     * memory (to help guard against memory-read attacks).
     */
    @JsonIgnore
    private final PublicKey publicKey;
    @JsonIgnore
    private final PrivateKey privateKey;
    @JsonInclude
    private final ZonedDateTime creationDate;
    @JsonInclude
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] address;
    @JsonInclude
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] walletHash;
    @JsonIgnore
    private final HashMap<byte[], WalletTransaction> wTxns;
    @JsonIgnore
    private Signature signature;

    private Wallet(Builder builder) throws NoSuchAlgorithmException {
        this.publicKey = builder.publicKey;
        this.privateKey = builder.privateKey;
        this.creationDate = builder.creationDate;
        this.address = builder.address;
        this.wTxns = new HashMap<>();
        this.signature = Signature.getInstance(CryptoKeyGenerator.getSignatureAlgorithm());
        this.walletHash = CryptoHasher.hash(this);
    }

    private Wallet(PublicKey publicKey, PrivateKey privateKey, ZonedDateTime creationDate, byte[] address, BigDecimal balance, byte[] walletHash) throws NoSuchAlgorithmException {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.signature = Signature.getInstance(CryptoKeyGenerator.getSignatureAlgorithm());
        this.creationDate = creationDate;
        this.address = address;
        this.wTxns = new HashMap<>();
        this.walletHash = walletHash;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public byte[] getAddress() {
        return address;
    }

    @JsonIgnore
    public BigDecimal getBalance() {
        BigDecimal bal = BigDecimal.ZERO;
        for(WalletTransaction wTxn : this.wTxns.values()) {
            bal = bal.add(wTxn.getCredits());
        }
        return bal;
    }

    public void signTransaction(Transaction txn) throws InvalidKeyException, SignatureException {
        signature.initSign(privateKey);
        byte[] txnData = txn.getTxnHash();
        signature.update(txnData, 0, txnData.length);
        txn.setSignature(signature.sign());
    }

    public byte[] getSignature(TransactionOutput txnOutpt) throws SignatureException, InvalidKeyException {
        signature.initSign(privateKey);
        byte[] sigRandBits = SerializationUtils.serialize(UUID.randomUUID());
        signature.update(sigRandBits, 0, sigRandBits.length);
        return signature.sign();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getWalletHash() {
        return walletHash;
    }

    @JsonIgnore
    public String getHumanReadableAddress() {
        return CryptoHasher.humanReadableHash(address);
    }

    @Override
    public String toString() {
        return CryptoHasher.humanReadableHash(walletHash);
    }

    /**
     * Builder class is the Wallet builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class Builder {

        private PublicKey publicKey;
        private PrivateKey privateKey;
        private ZonedDateTime creationDate;
        private byte[] address;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setKeyPair(KeyPair keyPair) {
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
            return this;
        }

        public Wallet build() throws NoSuchAlgorithmException, NoSuchProviderException {
            this.creationDate = DateTimeUtil.getCurrentTimestamp();
            this.address = CryptoHasher.walletHash(this.publicKey);
            return new Wallet(this);
        }

        private byte[] buildWalletAddress(byte[] publicKeyEncoded) {
            byte[] address = new byte[20];
            int j = 0;
            for(int i = publicKeyEncoded.length-20; i < publicKeyEncoded.length; i++){
                address[j] = publicKeyEncoded[i];
                j++;
            }
            return address;
        }
    }
}
