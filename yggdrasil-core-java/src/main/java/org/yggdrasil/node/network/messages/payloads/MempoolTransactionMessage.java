package org.yggdrasil.node.network.messages.payloads;

import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;

public class MempoolTransactionMessage implements MessagePayload {

    @NotNull
    private final int txnCount;
    @NotNull
    private final MempoolTransactionPayload[] txns;

    public MempoolTransactionMessage(Builder builder) {
        this.txnCount = builder.txnCount;
        this.txns = builder.txns;
    }

    public int getTxnCount() {
        return txnCount;
    }

    public MempoolTransactionPayload[] getTxns() {
        return txns;
    }

    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }

    public static class Builder {

        protected int txnCount;
        protected MempoolTransactionPayload[] txns;

        private Builder(){}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setTxnCount(int txnCount) {
            this.txnCount = txnCount;
            return this;
        }

        public Builder setTxns(MempoolTransactionPayload[] txns) {
            this.txns = txns;
            return this;
        }

        public MempoolTransactionMessage build() {
            return new MempoolTransactionMessage(this);
        }

    }

}
