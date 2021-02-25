package org.nathanielbunch.ssblockchain.node.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude
public class SSBlockResponse {

    private final UUID index;
    private final LocalDateTime timestamp;
    private final Long size;
    private final byte[] blockHash;

    private SSBlockResponse(Builder builder) {
        this.index = builder.index;
        this.timestamp = builder.timestamp;
        this.size = builder.size;
        this.blockHash = builder.blockhash;
    }

    public UUID getIndex() {
        return index;
    }

    public LocalDateTime getTimestamp() {
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
        private LocalDateTime timestamp;
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

        public Builder setTimestamp(LocalDateTime timestamp) {
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

        public SSBlockResponse build() {
            return new SSBlockResponse(this);
        }

    }
}
