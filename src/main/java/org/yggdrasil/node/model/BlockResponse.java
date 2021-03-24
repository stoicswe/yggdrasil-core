package org.yggdrasil.node.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * The block response is useful for returning some of the basic information about
 * recently acquired or mined blocks. This is the object returned to the calling REST
 * interface.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
@JsonInclude
public class BlockResponse {

    private final UUID index;
    private final ZonedDateTime timestamp;
    private final Long size;
    private final byte[] blockHash;

    private BlockResponse(Builder builder) {
        this.index = builder.index;
        this.timestamp = builder.timestamp;
        this.size = builder.size;
        this.blockHash = builder.blockhash;
    }

    public UUID getIndex() {
        return index;
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

        private UUID index;
        private ZonedDateTime timestamp;
        private Long size;
        private byte[] blockhash;

        private Builder(){}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setIndex(UUID index) {
            this.index = index;
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
