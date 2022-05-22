package org.yggdrasil.node.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.enums.GetDataType;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

/**
 * The Get Data message is used by a node to retrieve data from another node.
 * That data could be block related (blocks, chain, or transactions) and in that case
 * the object hashes will be populated. Otherwise, the message is a command message,
 * used by the distributed system for system data, such as IP addresses.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
@JsonInclude
public class BlockRequestMessage implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    private final int hashCount;
    @NotNull
    private final byte[][] objectHashes;
    @NotNull
    private final byte[] stopHash;

    private BlockRequestMessage(Builder builder) {
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

        public BlockRequestMessage build() {
            return new BlockRequestMessage(this);
        }

    }
}
