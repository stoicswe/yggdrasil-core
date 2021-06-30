package org.yggdrasil.node.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.yggdrasil.core.serialization.HashSerializer;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * The block response is useful for returning some of the basic information about
 * recently acquired or mined blocks. This is the object returned to the calling REST
 * interface.
 *
 * @since 0.0.6
 * @author nathanielbunch
 */
@JsonInclude
public class BlockResponse {

    private final BigInteger blockHeight;
    private final ZonedDateTime timestamp;
    private final Long size;
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] blockHash;

    private BlockResponse(Builder builder) {
        this.blockHeight = builder.blockHeight;
        this.timestamp = builder.timestamp;
        this.size = builder.size;
        this.blockHash = builder.blockhash;
    }

    public BigInteger getBlockHeight() {
        return this.blockHeight;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public long getSize() {
        return size;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    public static class Builder {

        private BigInteger blockHeight;
        private ZonedDateTime timestamp;
        private Long size;
        private byte[] blockhash;

        private Builder(){}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setBlockHeight(BigInteger blockHeight) {
            this.blockHeight = blockHeight;
            return this;
        }

        public Builder setTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setSize(Long size) {
            this.size = size;
            return this;
        }

        public Builder setBlockhash(byte[] blockhash) {
            this.blockhash = blockhash;
            return this;
        }

        public BlockResponse build() {
            return new BlockResponse(this);
        }

    }
}
