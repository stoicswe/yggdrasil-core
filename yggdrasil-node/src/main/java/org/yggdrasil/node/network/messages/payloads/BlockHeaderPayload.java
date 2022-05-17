package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * The Header Payload message contains the headers of either blocks or transactions.
 * When a block header is returned the hash, previousHash, transactionCount, time,
 * and nonce are populated, compared to a transaction header where only the hash,
 * previousHash, time and nonce are populated.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class BlockHeaderPayload implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    private final byte[] prevBlock;
    @NotNull
    private final byte[] merkleRoot;
    @NotNull
    private final int timestamp;
    @NotNull
    private final int diff;
    @NotNull
    private final int nonce;
    @NotNull
    private final int txnCount;

    private BlockHeaderPayload(Builder builder) {
        this.version = builder.version;
        this.prevBlock = builder.previousHash;
        this.merkleRoot = builder.merkleRoot;
        this.timestamp = builder.timestamp;
        this.diff = builder.diff;
        this.nonce = builder.nonce;
        this.txnCount = builder.transactionCount;
    }

    public int getVersion() {
        return version;
    }

    public byte[] getPrevBlock() {
        return prevBlock;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getDiff() {
        return diff;
    }

    public int getNonce() {
        return nonce;
    }

    public int getTxnCount() {
        return txnCount;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = appendBytes(messageBytes, prevBlock);
        messageBytes = appendBytes(messageBytes, merkleRoot);
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(diff));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(nonce));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(txnCount));
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private int version;
        private byte[] previousHash;
        private byte[] merkleRoot;
        private int timestamp;
        private int diff;
        private int nonce;
        private int transactionCount;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setVersion(BigInteger version) {
            this.version = version.intValue();
            return this;
        }

        public Builder setPreviousHash(byte[] previousHash) {
            this.previousHash = previousHash;
            return this;
        }

        public Builder setMerkleRoot(byte[] merkleRoot) {
            this.merkleRoot = merkleRoot;
            return this;
        }

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setDiff(int diff) {
            this.diff = diff;
            return this;
        }

        public Builder setNonce(int nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder setTxnCount(int transactionCount) {
            this.transactionCount = transactionCount;
            return this;
        }

        public BlockHeaderPayload build() {
            return new BlockHeaderPayload(this);
        }

    }
}
