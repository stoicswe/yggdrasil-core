package org.yggdrasil.core.ledger.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.yggdrasil.core.serialization.TransactionDeserializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.payloads.TransactionMessage;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
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

    private final UUID index;
    private final ZonedDateTime timestamp;
    private final byte[] origin;
    private final byte[] destination;
    private final BigDecimal value;
    private final String note;
    private final byte[] signature;
    private byte[] txnHash;
    private int nonce;

    protected Transaction(Builder builder) throws NoSuchAlgorithmException {
        this.index = builder.index;
        this.timestamp = builder.timestamp;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.value = builder.value;
        this.note = builder.note;
        this.signature = builder.signature;
        this.txnHash = CryptoHasher.hash(this);
    }

    public UUID getIndex() {
        return index;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public byte[] getOrigin() {
        return origin;
    }

    public byte[] getDestination() {
        return destination;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getNote() {
        return note;
    }

    public byte[] getTxnHash() {
        return txnHash;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] rehash() throws NoSuchAlgorithmException {
        this.txnHash = CryptoHasher.hash(this);
        return this.txnHash;
    }

    public void incrementNonce() {
        this.nonce++;
    }

    public int getNonce() {
        return nonce;
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

    @Override
    public String toString(){
        return CryptoHasher.humanReadableHash(txnHash);
    }

    /**
     * TBuilder class is the SSTransaction builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class Builder {

        protected UUID index;
        protected ZonedDateTime timestamp;
        protected byte[] origin;
        protected byte[] destination;
        protected BigDecimal value;
        protected String note;
        protected byte[] signature;

        private Builder(){}

        public Builder setOrigin(@JsonProperty("origin") String origin) {
            this.origin = CryptoHasher.hashByteArray(origin);
            return this;
        }

        public Builder setOrigin(byte[] origin) {
            this.origin = origin;
            return this;
        }

        public Builder setDestination(@JsonProperty("destination") String destination) {
            this.destination = CryptoHasher.hashByteArray(destination);
            return this;
        }

        public Builder setDestination(byte[] destination) {
            this.destination = destination;
            return this;
        }

        public Builder setValue(@JsonProperty("value") BigDecimal value) {
            this.value = value;
            return this;
        }

        public Builder setNote(@JsonProperty("note") String note) {
            this.note = note;
            return this;
        }

        public Builder setSignature(@JsonProperty("signature") String signature) {
            this.signature = CryptoHasher.hashByteArray(signature);
            return this;
        }

        public Builder setSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }

        public static Builder Builder() {
            return new Builder();
        }

        public Transaction build() throws NoSuchAlgorithmException {
            this.index = UUID.randomUUID();
            this.timestamp = DateTimeUtil.getCurrentTimestamp();
            return new Transaction(this);
        }

        public Transaction buildFromMessage(TransactionPayload transactionMessage) throws NoSuchAlgorithmException {
            this.index = UUID.fromString(String.valueOf(transactionMessage.getIndex()));
            this.timestamp = DateTimeUtil.fromMessageTimestamp(transactionMessage.getTimestamp());
            this.origin = transactionMessage.getOriginAddress();
            this.destination = transactionMessage.getDestinationAddress();
            this.value = transactionMessage.getValue();
            this.note = String.valueOf(transactionMessage.getNote());
            this.signature = transactionMessage.getSignature();
            return new Transaction(this);
        }
    }

}
