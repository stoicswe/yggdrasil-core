package org.yggdrasil.node.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashArraySerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class DataMessageRequest implements MessagePayload {

    @NotNull
    private int requestCount;
    @NotNull
    @JsonSerialize(using = HashArraySerializer.class)
    private byte[][] requestedTransactions;

    private DataMessageRequest(Builder builder) {
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
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(requestCount));
        for(byte[] t : requestedTransactions) {
            messageBytes = DataUtil.appendBytes(messageBytes, t);
        }
        return messageBytes;
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

        public DataMessageRequest build() {
            return new DataMessageRequest(this);
        }

    }
}
