package org.nathanielbunch.ssblockchain.core.ledger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.nathanielbunch.ssblockchain.core.utils.SSHasher;
import org.nathanielbunch.ssblockchain.node.config.serialization.TransactionDeserializer;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude
@JsonDeserialize(using = TransactionDeserializer.class)
public class SSTransaction implements Serializable {

    private final UUID index;
    private final LocalDateTime timestamp;
    private final String origin;
    private final String destination;
    private final Double amount;
    private final String note;
    private final byte[] transactionHash;

    @JsonCreator
    private SSTransaction(TBuilder builder) throws NoSuchAlgorithmException {
        this.index = builder.index;
        this.timestamp = builder.timestamp;
        this.origin = builder.originAddress;
        this.destination = builder.destinationAddress;
        this.amount = builder.amount;
        this.note = builder.note;
        this.transactionHash = SSHasher.hash(this);
    }

    public UUID getIndex() {
        return index;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public Double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public byte[] getTransactionHash() {
        return transactionHash;
    }

    @Override
    public String toString(){
        return SSHasher.humanReadableHash(transactionHash);
    }

    public static class TBuilder {

        private UUID index;
        private LocalDateTime timestamp;
        private String originAddress;
        private String destinationAddress;
        private Double amount;
        private String note;

        private TBuilder(){}

        public TBuilder setIndex(UUID index) {
            this.index = index;
            return this;
        }

        public TBuilder setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public TBuilder setOrigin(String originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public TBuilder setDestination(String destinationAddress) {
            this.destinationAddress = destinationAddress;
            return this;
        }

        public TBuilder setAmountValue(Double amountValue) {
            this.amount = amountValue;
            return this;
        }

        public TBuilder setNote(String note) {
            this.note = note;
            return this;
        }

        public static TBuilder newSSTransactionBuilder() {
            return new TBuilder();
        }

        public SSTransaction build() throws NoSuchAlgorithmException {
            if(this.index == null) {
                this.index = UUID.randomUUID();
            }
            if(this.timestamp == null) {
                timestamp = LocalDateTime.now();
            }
            return new SSTransaction(this);
        }
    }
}
