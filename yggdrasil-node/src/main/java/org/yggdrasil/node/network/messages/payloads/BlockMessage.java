package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

/**
 * Used for transmitting individual block data.
 *
 * @since 0.0.16
 * @author nathanielbunch
 */
@JsonInclude
public class BlockMessage implements MessagePayload {

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
    @NotNull
    private final TransactionPayload[] txnPayloads;


    private BlockMessage(Builder builder) {
        this.version = builder.version;
        this.prevBlock = builder.previousBlock;
        this.merkleRoot = builder.merkleRoot;
        this.timestamp = builder.timestamp;
        this.diff = builder.diff;
        this.nonce = builder.nonce;
        this.txnCount = builder.txnCount;
        this.txnPayloads = builder.txnPayloads;
    }

    public int getVersion() {
        return version;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public byte[] getPrevBlock() {
        return prevBlock;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
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

    public TransactionPayload[] getTxnPayloads() {
        return txnPayloads;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        messageBytes = DataUtil.appendBytes(messageBytes, prevBlock);
        messageBytes = DataUtil.appendBytes(messageBytes, merkleRoot);
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(diff));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(nonce));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(txnCount));
        for(TransactionPayload txnPayload : txnPayloads) {
            messageBytes = DataUtil.appendBytes(messageBytes, txnPayload.getDataBytes());
        }
        return messageBytes;
    }

    public static class Builder {
        protected int version;
        protected byte[] previousBlock;
        protected byte[] merkleRoot;
        protected int timestamp;
        protected int diff;
        protected int nonce;
        protected int txnCount;
        protected TransactionPayload[] txnPayloads;

        private Builder(){}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setPreviousBlock(byte[] previousBlock) {
            this.previousBlock = previousBlock;
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

        public Builder setTxnPayloads(TransactionPayload[] txnPayloads) {
            this.txnPayloads = txnPayloads;
            return this;
        }

        public Builder setTxnCount(int txnCount) {
            this.txnCount = txnCount;
            return this;
        }

        public BlockMessage build() {
            return new BlockMessage(this);
        }
    }
}
