package org.yggdrasil.node.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.yggdrasil.core.serialization.HashArraySerializer;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;

@JsonInclude
public class DataRequestMessage implements MessagePayload {

    @NotNull
    private int requestCount;
    @NotNull
    @JsonSerialize(using = HashArraySerializer.class)
    private byte[][] requestedTransactions;

    private DataRequestMessage(Builder builder) {
        this.requestCount = builder.requestCount;
        this.requestedTransactions = builder.requestedTransactions;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public byte[][] getRequestedTransactions() {
        return requestedTransactions;
    }

    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }

    public static class Builder {

        private int requestCount;
        private byte[][] requestedTransactions;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setRequestedTransactions(byte[][] requestedTransactions) {
            this.requestCount = requestedTransactions.length;
            this.requestedTransactions = requestedTransactions;
            return this;
        }

        public DataRequestMessage build() {
            return new DataRequestMessage(this);
        }

    }
}
