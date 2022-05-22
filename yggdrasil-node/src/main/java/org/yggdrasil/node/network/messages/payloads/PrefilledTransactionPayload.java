package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class PrefilledTransactionPayload implements MessagePayload {

    @NotNull
    private int index;
    @NotNull
    private TransactionPayload transaction;

    public PrefilledTransactionPayload(Builder builder) {
        this.index = builder.index;
        this.transaction = builder.transaction;
    }

    public int getIndex() {
        return index;
    }

    public TransactionPayload getTransactions() {
        return transaction;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(index));
        messageBytes = DataUtil.appendBytes(messageBytes, transaction.getDataBytes());
        return messageBytes;
    }

    public static class Builder {

        private int index;
        private TransactionPayload transaction;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setIndex(int index) {
            this.index = index;
            return this;
        }

        public Builder setTransaction(TransactionPayload transaction) {
            this.transaction = transaction;
            return this;
        }

        public PrefilledTransactionPayload build() {
            return new PrefilledTransactionPayload(this);
        }

    }
}
