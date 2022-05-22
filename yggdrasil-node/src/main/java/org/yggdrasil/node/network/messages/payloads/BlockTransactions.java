package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class BlockTransactions implements MessagePayload {

    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private byte[] blockHash;
    @NotNull
    private int transactionsLength;
    @NotNull
    private TransactionPayload[] transactions;

    private BlockTransactions(Builder builder) {
        this.blockHash = builder.blockHash;
        this.transactionsLength = builder.transactionsLength;
        this.transactions = builder.transactions;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    public int getTransactionsLength() {
        return transactionsLength;
    }

    public TransactionPayload[] getTransactions() {
        return transactions;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, blockHash);
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(transactionsLength));
        for(TransactionPayload t : this.transactions) {
            messageBytes = DataUtil.appendBytes(messageBytes, t.getDataBytes());
        }
        return messageBytes;
    }

    public static class Builder {

        private byte[] blockHash;
        private int transactionsLength;
        private TransactionPayload[] transactions;

        public Builder builder() {
            return new Builder();
        }

        public Builder setBlockHash(byte[] blockHash) {
            this.blockHash = blockHash;
            return this;
        }

        public Builder setTransactions(TransactionPayload[] transactions) {
            this.transactionsLength = transactions.length;
            this.transactions = transactions;
            return this;
        }

        public BlockTransactions build() {
            return new BlockTransactions(this);
        }

    }
}
