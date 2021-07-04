package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.transaction.TransactionInput;
import org.yggdrasil.core.ledger.transaction.TransactionOutput;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;

/**
 * The Transaction Message contains the full information of a transaction.
 *
 * @since 0.0.15
 * @author nathanielbunch
 */
public class TransactionPayload implements MessagePayload {

    @NotNull
    private final int timestamp;
    @NotNull
    private final byte[] originAddress;
    @NotNull
    private final byte[] originPublicKey;
    @NotNull
    private final char[] destinationAddress;
    @NotNull
    private final TransactionInput[] txnIn;
    @NotNull
    private final TransactionOutput[] txnOut;
    @NotNull
    private final byte[] transactionHash;
    @NotNull
    private final byte[] signature;
    @NotNull
    private final byte[] merkleRoot;
    @NotNull
    private final byte[] blockHash;

    private TransactionPayload(Builder builder){
        this.timestamp = builder.timestamp;
        this.originAddress = builder.originAddress;
        this.originPublicKey = builder.originPublicKey;
        this.destinationAddress = builder.destinationAddress;
        this.txnIn = builder.txnIn;
        this.txnOut = builder.txnOut;
        this.merkleRoot = builder.merkleRoot;
        this.transactionHash = builder.transactionHash;
        this.signature = builder.signature;
        this.blockHash = builder.blockHash;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public byte[] getOriginAddress() {
        return originAddress;
    }

    public char[] getDestinationAddress() {
        return destinationAddress;
    }

    public byte[] getOriginPublicKey() {
        return originPublicKey;
    }

    public TransactionInput[] getTxnIn() {
        return txnIn;
    }

    public TransactionOutput[] getTxnOut() {
        return txnOut;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public byte[] getTransactionHash() {
        return transactionHash;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(originAddress));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(originPublicKey));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(destinationAddress));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(txnIn));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(txnOut));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(merkleRoot));
        messageBytes = appendBytes(messageBytes, transactionHash);
        messageBytes = appendBytes(messageBytes, signature);
        messageBytes = appendBytes(messageBytes, blockHash);
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private int timestamp;
        private byte[] originAddress;
        private byte[] originPublicKey;
        private char[] destinationAddress;
        private TransactionInput[] txnIn;
        private TransactionOutput[] txnOut;
        private byte[] merkleRoot;
        private byte[] transactionHash;
        private byte[] signature;
        private byte[] blockHash;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setOriginAddress(byte[] originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public Builder setOriginPublicKey(byte[] originPublicKey) {
            this.originPublicKey = originPublicKey;
            return this;
        }

        public Builder setDestinationAddress(char[] destinationAddress) {
            this.destinationAddress = destinationAddress;
            return this;
        }

        public Builder setTransactionInput(TransactionInput[] txnIn) {
            this.txnIn = txnIn;
            return this;
        }

        public Builder setTransactionOutput(TransactionOutput[] txnOut) {
            this.txnOut = txnOut;
            return this;
        }

        public Builder setMerkleRoot(byte[] merkleRoot) {
            this.merkleRoot = merkleRoot;
            return this;
        }

        public Builder setTransactionHash(byte[] transactionHash) {
            this.transactionHash = transactionHash;
            return this;
        }

        public Builder setSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }

        public Builder setBlockHash(byte[] blockHash) {
            this.blockHash = blockHash;
            return this;
        }

        public Builder buildFromTransaction(Transaction txn) {
            this.timestamp = (int) txn.getTimestamp().toEpochSecond();
            this.originAddress = txn.getOrigin().getEncoded();
            this.destinationAddress = txn.getDestinationAddress().toCharArray();
            this.transactionHash = txn.getTxnHash();
            this.signature = txn.getSignature();
            return this;
        }

        public TransactionPayload build() {
            return new TransactionPayload(this);
        }

    }

}
