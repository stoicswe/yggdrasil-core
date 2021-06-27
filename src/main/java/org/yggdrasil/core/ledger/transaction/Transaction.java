package org.yggdrasil.core.ledger.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.core.serialization.TransactionDeserializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.payloads.TransactionMessage;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Every Block is made of n number of Transactions. Transactions contain
 * information used for identifying a transaction (index), a timestamp for
 * sorting and managing transactions in a block, an origin address, a
 * destination address, an amount of coin transmitted, a transaction note, and
 * the identifying transaction hash. Transactions can be queried for by their
 * hash or their index and timestamp.
 *
 * @since 0.0.2
 * @author nathanielbunch
 */
@JsonInclude
@JsonDeserialize(using = TransactionDeserializer.class)
public class Transaction implements Serializable {

    // Receiver of transaction has to sign the incoming transaction
    // that way it can be varified that it should be received.

    private final ZonedDateTime timestamp;
    @JsonSerialize(using = HashSerializer.class)
    private final PublicKey origin;
    @JsonSerialize(using = HashSerializer.class)
    private final PublicKey destination;
    private final TransactionInput[] txnInputs;
    private final TransactionOutput[] txnOutPuts;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] signature;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] txnHash;

    protected Transaction(Builder builder) throws NoSuchAlgorithmException {
        this.timestamp = builder.timestamp;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.txnInputs = builder.txnInputs;
        this.txnOutPuts = builder.txnOutPuts;
        this.txnHash = CryptoHasher.hash(this);
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public PublicKey getOrigin() {
        return origin;
    }

    public PublicKey getDestination() {
        return destination;
    }

    public byte[] getTxnHash() {
        return txnHash;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
    }

    public TransactionInput[] getTxnInputs() {
        return txnInputs;
    }

    public TransactionOutput[] getTxnOutPuts() {
        return txnOutPuts;
    }

    public byte[] rehash() throws NoSuchAlgorithmException {
        this.txnHash = CryptoHasher.hash(this);
        return this.txnHash;
    }

    public boolean compareTxnHash(byte[] txnHash) {
        try {
            for (int i = 0; i < txnHash.length; i++) {
                if (this.txnHash[i] != txnHash[i]) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public boolean isCoinbase() {
        return (this.txnInputs.length == 1 && this.txnInputs[0].txnOutPt == null);
    }

    @Override
    public String toString(){
        return CryptoHasher.humanReadableHash(txnHash);
    }

    /**
     * TBuilder class is the SSTransaction builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class Builder {

        protected ZonedDateTime timestamp;
        protected PublicKey origin;
        protected PublicKey destination;
        protected TransactionInput[] txnInputs;
        protected TransactionOutput[] txnOutPuts;

        private Builder(){}

        public Builder setOrigin(@JsonProperty("origin") String origin) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            byte[] publicKey = CryptoHasher.hashByteArray(origin);
            this.origin = CryptoKeyGenerator.readPublicKeyFromBytes(publicKey);
            return this;
        }

        public Builder setOrigin(byte[] origin) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            this.origin = CryptoKeyGenerator.readPublicKeyFromBytes(origin);
            return this;
        }

        public Builder setDestination(@JsonProperty("destination") String destination) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            byte[] publicKey = CryptoHasher.hashByteArray(destination);
            this.destination = CryptoKeyGenerator.readPublicKeyFromBytes(publicKey);
            return this;
        }

        public Builder setDestination(byte[] destination) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            this.destination = CryptoKeyGenerator.readPublicKeyFromBytes(destination);
            return this;
        }

        public Builder setTxnInputs(TransactionInput[] txnInputs){
            this.txnInputs = txnInputs;
            return this;
        }

        public Builder setTxnOutputs(TransactionOutput[] txnOutPuts) {
            this.txnOutPuts = txnOutPuts;
            return this;
        }

        public static Builder Builder() {
            return new Builder();
        }

        public Transaction build() throws NoSuchAlgorithmException {
            this.timestamp = DateTimeUtil.getCurrentTimestamp();
            return new Transaction(this);
        }

        public Transaction buildFromMessage(TransactionPayload transactionMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            this.timestamp = DateTimeUtil.fromMessageTimestamp(transactionMessage.getTimestamp());
            this.origin = CryptoKeyGenerator.readPublicKeyFromBytes(transactionMessage.getOriginAddress());
            this.destination = CryptoKeyGenerator.readPublicKeyFromBytes(transactionMessage.getDestinationAddress());
            Transaction txn = new Transaction(this);
            txn.signature = transactionMessage.getSignature();
            return txn;
        }
    }

}
