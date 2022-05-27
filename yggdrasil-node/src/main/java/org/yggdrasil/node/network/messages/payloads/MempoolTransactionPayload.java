package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class MempoolTransactionPayload implements MessagePayload {

    @NotNull
    private int transactionsLength;
    @NotNull
    private TransactionPayload[] transactions;
    @NotNull
    private byte[] requestChecksum;

    private MempoolTransactionPayload(Builder builder) {
        this.transactionsLength = builder.transactionsLength;
        this.transactions = builder.transactions;
        this.requestChecksum = builder.requestChecksum;
    }

    public int getTransactionsLength() {
        return transactionsLength;
    }

    public TransactionPayload[] getTransactions() {
        return transactions;
    }

    public byte[] getRequestChecksum() {
        return requestChecksum;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(transactionsLength));
        for (TransactionPayload t : this.transactions) {
            messageBytes = DataUtil.appendBytes(messageBytes, t.getDataBytes());
        }
        messageBytes = DataUtil.appendBytes(messageBytes, requestChecksum);
        return messageBytes;
    }

    public static class Builder {
        private int transactionsLength;
        private TransactionPayload[] transactions;
        private byte[] requestChecksum;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setTransactions(TransactionPayload[] transactions) {
            this.transactionsLength = transactions.length;
            this.transactions = transactions;
            return this;
        }

        public Builder setRequestChecksum(byte[] requestChecksum) {
            this.requestChecksum = requestChecksum;
            return this;
        }

        public MempoolTransactionPayload build() {
            return new MempoolTransactionPayload(this);
        }

    }
}