package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class TransactionPayload implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    private final boolean isWitness;
    @NotNull
    private final int txInCount;
    @NotNull
    private final TransactionIn[] txnIn;
    @NotNull
    private final int txOutCount;
    @NotNull
    private final TransactionOut[] txOut;
    // TODO: Fully implement the witness
    @NotNull
    private final TransactionWitness[] witnesses;
    @NotNull
    private final int lockTime;

    public TransactionPayload(Builder builder) {
        this.version = builder.version;
        this.isWitness = builder.isWitness;
        this.txInCount = builder.txInCount;
        this.txnIn = builder.txnIn;
        this.txOutCount = builder.txOutCount;
        this.txOut = builder.txOut;
        this.witnesses = builder.witnesses;
        this.lockTime = builder.lockTime;
    }

    public int getVersion() {
        return version;
    }

    public boolean isWitness() {
        return isWitness;
    }

    public int getTxInCount() {
        return txInCount;
    }

    public TransactionIn[] getTxnIn() {
        return txnIn;
    }

    public int getTxOutCount() {
        return txOutCount;
    }

    public TransactionOut[] getTxOut() {
        return txOut;
    }

    /*
    public TransactionWitness[] getWitnesses() {
        return witnesses;
    }
    */

    public int getLockTime() {
        return lockTime;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(isWitness));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(txInCount));
        for(TransactionIn txin : txnIn) {
            messageBytes = DataUtil.appendBytes(messageBytes, txin.getDataBytes());
        }
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(txOutCount));
        for(TransactionOut txOut : txOut) {
            messageBytes = DataUtil.appendBytes(messageBytes, txOut.getDataBytes());
        }
        for(TransactionWitness witness : witnesses) {
            messageBytes = DataUtil.appendBytes(messageBytes, witness.getDataBytes());
        }
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(lockTime));
        return messageBytes;
    }

    public static class Builder {
        private int version;
        private boolean isWitness;
        private int txInCount;
        private TransactionIn[] txnIn;
        private int txOutCount;
        private TransactionOut[] txOut;
        private TransactionWitness[] witnesses;
        private int lockTime;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setVersion(int version){
            this.version = version;
            return this;
        }

        public Builder setWitnessFlag(boolean isWitness) {
            this.isWitness = false;
            return this;
        }

        public Builder setTxIns(TransactionIn[] txIn) {
            this.txInCount = txIn.length;
            this.txnIn = txIn;
            return this;
        }

        public Builder setTxOuts(TransactionOut[] txOut) {
            this.txOutCount = txOut.length;
            this.txOut = txOut;
            return this;
        }

        public Builder setWitnesses(TransactionWitness[] witnesses) {
            this.witnesses = witnesses;
            return this;
        }

        public Builder setLockTime(int lockTime) {
            this.lockTime = lockTime;
            return this;
        }

        public TransactionPayload build() {
            return new TransactionPayload(this);
        }

        public TransactionPayload buildFromTxn(Transaction txn) {
            //TODO: Fix this
            return null;
        }
    }
}
