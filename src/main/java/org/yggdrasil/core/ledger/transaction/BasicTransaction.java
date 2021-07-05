package org.yggdrasil.core.ledger.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.ledger.LedgerHashableItem;
import org.yggdrasil.core.serialization.BasicTransactionDeserializer;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

@JsonInclude
@JsonDeserialize(using = BasicTransactionDeserializer.class)
public class BasicTransaction implements LedgerHashableItem {

    private final ZonedDateTime timestamp;
    private final String originAddress;
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

    @JsonIgnore
    @Override
    public byte[] getDataBytes() {
        byte[] txnData = new byte[0];
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.timestamp));
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.originAddress));
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.destinationAddress));
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.value));
        return txnData;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        protected ZonedDateTime timestamp = null;
        protected String originAddress;
        protected String destinationAddress;
        protected BigDecimal value;

        private Builder(){}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
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
            this.timestamp = (timestamp != null) ? this.timestamp : DateTimeUtil.getCurrentTimestamp();
            return new BasicTransaction(this);
        }

    }


}
