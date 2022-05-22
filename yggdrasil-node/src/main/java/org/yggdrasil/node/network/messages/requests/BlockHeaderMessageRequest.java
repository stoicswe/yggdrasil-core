package org.yggdrasil.node.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashArraySerializer;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class BlockHeaderMessageRequest implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    private final int hashCount;
    @NotNull
    @JsonSerialize(using = HashArraySerializer.class)
    private final byte[][] objectHashes;
    // If the stopHash is empty, then grab the maximum blocks possible in response
    // with the maximum set to 2000.
    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] stopHash;

    private BlockHeaderMessageRequest(Builder builder) {
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
        for(byte[] h : objectHashes) {
            messageBytes = DataUtil.appendBytes(messageBytes, h);
        }
        messageBytes = DataUtil.appendBytes(messageBytes, stopHash);
        return messageBytes;
    }

    public static class Builder {

        private int version;
        private int hashCount;
        private byte[][] objectHashes;
        private byte[] stopHash;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setObjectHashes(byte[][] objectHashes) {
            this.hashCount = objectHashes.length;
            this.objectHashes = objectHashes;
            return this;
        }

        public Builder setStopHash(byte[] stopHash) {
            this.stopHash = stopHash;
            return this;
        }

        public BlockHeaderMessageRequest build() {
            return new BlockHeaderMessageRequest(this);
        }
    }
}
