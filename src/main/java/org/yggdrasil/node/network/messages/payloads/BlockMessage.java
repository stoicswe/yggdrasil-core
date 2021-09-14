package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.UUID;

/**
 * Used for transmitting individual block data.
 *
 * @since 0.0.16
 * @author nathanielbunch
 */
public class BlockMessage implements MessagePayload {

    @NotNull
    private final int blockHeight;
    @NotNull
    private final int timestamp;
    @NotNull
    private final TransactionPayload[] txnPayloads;
    @NotNull
    private final byte[] previousBlockHash;
    @NotNull
    private final byte[] blockHash;
    @NotNull
    private final byte[] merkleRoot;
    @NotNull
    private final byte[] signature;
    @NotNull
    private final int nonce;

    private BlockMessage(Builder builder) {
        this.blockHeight = builder.blockHeight;
        this.timestamp = builder.timestamp;
        this.txnPayloads = builder.txnPayloads;
        this.previousBlockHash = builder.previousBlockHash;
        this.blockHash = builder.blockHash;
        this.merkleRoot = builder.merkleRoot;
        this.signature = builder.signature;
        this.nonce = builder.nonce;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public TransactionPayload[] getTxnPayloads() {
        return txnPayloads;
    }

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public byte[] getSignature() {
        return signature;
    }

    public int getNonce() {
        return nonce;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(blockHeight));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        for(TransactionPayload txnPayload : txnPayloads) {
            messageBytes = appendBytes(messageBytes, txnPayload.getDataBytes());
        }
        messageBytes = appendBytes(messageBytes, previousBlockHash);
        messageBytes = appendBytes(messageBytes, blockHash);
        messageBytes = appendBytes(messageBytes, merkleRoot);
        messageBytes = appendBytes(messageBytes, signature);
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(nonce));
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {
        protected int blockHeight;
        protected int timestamp;
        protected TransactionPayload[] txnPayloads;
        protected byte[] previousBlockHash;
        protected byte[] blockHash;
        protected byte[] merkleRoot;
        protected byte[] signature;
        protected int nonce;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setBlockHeight(BigInteger blockHeight) {
            this.blockHeight = blockHeight.intValue();
            return this;
        }

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setTxnPayloads(TransactionPayload[] txnPayloads) {
            this.txnPayloads = txnPayloads;
            return this;
        }

        public Builder setPreviousBlockHash(byte[] previousBlockHash) {
            this.previousBlockHash = previousBlockHash;
            return this;
        }

        public Builder setBlockHash(byte[] blockHash) {
            this.blockHash = blockHash;
            return this;
        }

        public Builder setMerkleRoot(byte[] merkleRoot) {
            this.merkleRoot = merkleRoot;
            return this;
        }

        public Builder setSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }

        public BlockMessage build() {
            return new BlockMessage(this);
        }
    }
}
