package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

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
@JsonInclude
public class BlockHeaderPayload implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] prevBlock;
    @NotNull
    @JsonSerialize(using = HashSerializer.class)
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
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = DataUtil.appendBytes(messageBytes, prevBlock);
        messageBytes = DataUtil.appendBytes(messageBytes, merkleRoot);
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(diff));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(nonce));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(txnCount));
        return messageBytes;
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

        public static Builder builder() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            this.version = version;
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
