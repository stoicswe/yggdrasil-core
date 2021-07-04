package org.yggdrasil.core.ledger.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.yggdrasil.core.serialization.BasicTransactionDeserializer;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

@JsonInclude
@JsonDeserialize(using = BasicTransactionDeserializer.class)
public class BasicTransaction {

    private final ZonedDateTime timestamp;
    @JsonSerialize(using = HashSerializer.class)
    private final String originAddress;
    @JsonSerialize(using = HashSerializer.class)
    private final String destinationAddress;
    private final BigDecimal value;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] txnHash;

    protected BasicTransaction(Builder builder) throws NoSuchAlgorithmException {
        this.timestamp = builder.timestamp;
        this.originAddress = builder.originAddress;
        this.destinationAddress = builder.destinationAddress;
        this.value = builder.value;
        this.txnHash = CryptoHasher.hash(this);
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public BigDecimal getValue() {
        return value;
    }

    public byte[] getTxnHash() {
        return txnHash;
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

    public static class Builder {

        protected ZonedDateTime timestamp;
        protected String originAddress;
        protected String destinationAddress;
        protected BigDecimal value;

        private Builder(){}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setOrigin(@JsonProperty("origin") String originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public Builder setDestination(@JsonProperty("destination") String destinationAddress) {
            this.destinationAddress = destinationAddress;
            return this;
        }

        public Builder setValue(BigDecimal value) {
            this.value = value;
            return this;
        }

        public BasicTransaction build() throws NoSuchAlgorithmException {
            this.timestamp = DateTimeUtil.getCurrentTimestamp();
            return new BasicTransaction(this);
        }

    }


}
