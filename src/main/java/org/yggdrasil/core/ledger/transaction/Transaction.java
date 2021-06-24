package org.yggdrasil.core.ledger.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.yggdrasil.core.serialization.HashSerializer;
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

    private final ZonedDateTime timestamp;
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] origin;
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] destination;
    private final BigDecimal value;

    private final BigDecimal fee;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] signature;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] txnHash;

    protected Transaction(Builder builder) throws NoSuchAlgorithmException {
        this.timestamp = builder.timestamp;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.value = builder.value;
        this.fee = builder.fee;
        this.txnHash = CryptoHasher.hash(this);
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

    public byte[] getTxnHash() {
        return txnHash;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
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
        protected byte[] origin;
        protected byte[] destination;
        protected BigDecimal value;
        protected BigDecimal fee;

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

        public Builder setFee(@JsonProperty("fee") BigDecimal fee) {
            this.fee = fee;
            return this;
        }

        public static Builder Builder() {
            return new Builder();
        }

        public Transaction build() throws NoSuchAlgorithmException {
            this.timestamp = DateTimeUtil.getCurrentTimestamp();
            return new Transaction(this);
        }

        public Transaction buildFromMessage(TransactionPayload transactionMessage) throws NoSuchAlgorithmException {
            this.timestamp = DateTimeUtil.fromMessageTimestamp(transactionMessage.getTimestamp());
            this.origin = transactionMessage.getOriginAddress();
            this.destination = transactionMessage.getDestinationAddress();
            this.value = transactionMessage.getValue();
            Transaction txn = new Transaction(this);
            txn.signature = transactionMessage.getSignature();
            return txn;
        }
    }

}
