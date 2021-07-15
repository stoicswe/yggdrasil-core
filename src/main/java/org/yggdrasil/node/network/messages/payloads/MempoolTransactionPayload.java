package org.yggdrasil.node.network.messages.payloads;

import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.transaction.TransactionInput;
import org.yggdrasil.core.ledger.transaction.TransactionOutput;
import org.yggdrasil.core.utils.CryptoHasher;

import javax.validation.constraints.NotNull;

public class MempoolTransactionPayload {

    @NotNull
    private final int timestamp;
    @NotNull
    private final char[] originAddress;
    @NotNull
    private final char[] originPublicKey;
    @NotNull
    private final char[] destinationAddress;
    @NotNull
    private final TransactionInput[] txnIn;
    @NotNull
    private final TransactionOutput[] txnOut;
    @NotNull
    private final byte[] txnHash;
    @NotNull
    private final byte[] signature;

    public MempoolTransactionPayload(Builder builder) {
        this.timestamp = builder.timestamp;
        this.originAddress = builder.originAddress;
        this.originPublicKey = builder.originPublicKey;
        this.destinationAddress = builder.destinationAddress;
        this.txnIn = builder.txnIn;
        this.txnOut = builder.txnOut;
        this.signature = builder.signature;
        this.txnHash = builder.txnHash;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public char[] getOriginAddress() {
        return originAddress;
    }

    public char[] getOriginPublicKey() {
        return originPublicKey;
    }

    public char[] getDestinationAddress() {
        return destinationAddress;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getTxnHash() {
        return txnHash;
    }

    public static class Builder {
        protected int timestamp;
        protected char[] originAddress;
        protected char[] originPublicKey;
        protected char[] destinationAddress;
        protected TransactionInput[] txnIn;
        protected TransactionOutput[] txnOut;
        protected byte[] signature;
        protected byte[] txnHash;

        private Builder() {}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setOriginAddress(char[] originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public Builder setOriginPublicKey(char[] originPublicKey) {
            this.originPublicKey = originPublicKey;
            return this;
        }

        public Builder setDestinationAddress(char[] destinationAddress) {
            this.destinationAddress = destinationAddress;
            return this;
        }

        public Builder setTxnIn(TransactionInput[] txnIn) {
            this.txnIn = txnIn;
            return this;
        }

        public Builder setTxnOut(TransactionOutput[] txnOut) {
            this.txnOut = txnOut;
            return this;
        }

        public Builder setSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }

        public Builder setTxnHash(byte[] txnHash) {
            this.txnHash = txnHash;
            return this;
        }

        public MempoolTransactionPayload buildFromMempool(Transaction txn) {
            this.timestamp = (int) txn.getTimestamp().toEpochSecond();
            this.originAddress = txn.getOriginAddress().toCharArray();
            this.originPublicKey = CryptoHasher.humanReadableHash(txn.getOrigin().getEncoded()).toCharArray();
            this.destinationAddress = txn.getDestinationAddress().toCharArray();
            this.signature = txn.getSignature();
            this.txnHash = txn.getTxnHash();
            return new MempoolTransactionPayload(this);
        }

        public MempoolTransactionPayload build() {
            return new MempoolTransactionPayload(this);
        }
    }
}
