package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;

/**
 * The Header Payload message contains the headers of either blocks or transactions.
 * When a block header is returned the hash, previousHash, transactionCount, time,
 * and nonce are populated, compared to a transaction header where only the hash,
 * previousHash, time and nonce are populated.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class HeaderPayload implements MessagePayload {

    @NotNull
    private final char[] index;
    @NotNull
    private final byte[] hash;
    @NotNull
    private final byte[] prevHash;
    @NotNull
    private final int transactionCount;
    @NotNull
    private final int time;
    @NotNull
    private final int nonce;

    private HeaderPayload(Builder builder) {
        this.index = builder.index;
        this.hash = builder.hash;
        this.prevHash = builder.previousHash;
        this.transactionCount = builder.transactionCount;
        this.time = builder.time;
        this.nonce = builder.nonce;
    }

    public char[] getIndex() {
        return index;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getPrevHash() {
        return prevHash;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public int getTime() {
        return time;
    }

    public int getNonce() {
        return nonce;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(index));
        messageBytes = appendBytes(messageBytes, hash);
        messageBytes = appendBytes(messageBytes, prevHash);
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(transactionCount));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(time));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(nonce));
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private char[] index;
        private byte[] hash;
        private byte[] previousHash;
        private int transactionCount;
        private int time;
        private int nonce;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setIndex(char[] index) {
            this.index = index;
            return this;
        }

        public Builder setHash(byte[] hash) {
            this.hash = hash;
            return this;
        }

        public Builder setPreviousHash(byte[] previousHash) {
            this.previousHash = previousHash;
            return this;
        }

        public Builder setTransactionCount(int transactionCount) {
            this.transactionCount = transactionCount;
            return this;
        }

        public Builder setTime(int time) {
            this.time = time;
            return this;
        }

        public Builder setNonce(int nonce) {
            this.nonce = nonce;
            return this;
        }

        public HeaderPayload build() {
            return new HeaderPayload(this);
        }

    }
}
