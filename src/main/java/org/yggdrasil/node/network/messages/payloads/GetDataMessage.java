package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

/**
 * The Get Data message is used by a node to retrieve data from another node.
 * That data could be block related (blocks, chain, or transactions) and in that case
 * the object hashes will be populated. Otherwise, the message is a command message,
 * used by the distributed system for system data, such as IP addresses.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class GetDataMessage implements MessagePayload {

    private final int version;
    private final char[] type;
    private final int hashCount;
    private final char[][] objectHashes;
    private final char[] stopHash;

    private GetDataMessage(Builder builder) {
        this.version = builder.version;
        this.type = builder.type;
        this.hashCount = builder.hashCount;
        this.objectHashes = builder.objectHashes;
        this.stopHash = builder.stopHash;
    }

    public int getVersion() {
        return version;
    }

    public char[] getType() {
        return type;
    }

    public int getHashCount() {
        return hashCount;
    }

    public char[][] getObjectHashes() {
        return objectHashes;
    }

    public char[] getStopHash() {
        return stopHash;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(type));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(hashCount));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(objectHashes));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(stopHash));
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private int version;
        private char[] type;
        private int hashCount;
        private char[][] objectHashes;
        private char[] stopHash;

        private Builder(){}

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setType(char[] type) {
            this.type = type;
            return this;
        }

        public Builder setHashCount(int hashCount) {
            this.hashCount = hashCount;
            return this;
        }

        public Builder setObjectHashes(char[][] objectHashes) {
            this.objectHashes = objectHashes;
            return this;
        }

        public Builder setStopHash(char[] stopHash) {
            this.stopHash = stopHash;
            return this;
        }

    }
}
