package org.yggdrasil.node.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashArraySerializer;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

/**
 * Requests made to retrieve blocks will result with an inv message response,
 * containing records for complete block payloads.
 *
 * @author nathanielbunch
 */
@JsonInclude
public class BlockMessageRequest implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    private final int hashCount;
    @NotNull
    @JsonSerialize(using = HashArraySerializer.class)
    private final byte[][] objectHashes;
    // If the stopHash is empty, then grab the maximum blocks possible in response
    // with the maximum set to 500.
    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] stopHash;

    private BlockMessageRequest(Builder builder) {
        this.version = builder.version;
        this.hashCount = builder.hashCount;
        this.objectHashes = builder.objectHashes;
        this.stopHash = builder.stopHash;
    }

    public int getVersion() {
        return version;
    }

    public int getHashCount() {
        return hashCount;
    }

    public byte[][] getObjectHashes() {
        return objectHashes;
    }

    public byte[] getStopHash() {
        return stopHash;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(hashCount));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(objectHashes));
        messageBytes = DataUtil.appendBytes(messageBytes, stopHash);
        return messageBytes;
    }

    public static class Builder {

        private int version;
        private int hashCount;
        private byte[][] objectHashes;
        private byte[] stopHash;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setHashCount(int hashCount) {
            this.hashCount = hashCount;
            return this;
        }

        public Builder setObjectHashes(byte[][] objectHashes) {
            this.objectHashes = objectHashes;
            return this;
        }

        public Builder setStopHash(byte[] stopHash) {
            this.stopHash = stopHash;
            return this;
        }

        public BlockMessageRequest build() {
            return new BlockMessageRequest(this);
        }

    }
}
