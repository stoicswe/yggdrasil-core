package org.yggdrasil.node.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.payloads.InventoryVector;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class DataMessageRequest implements MessagePayload {

    @NotNull
    private int requestCount;
    @NotNull
    private InventoryVector[] requestedData;

    private DataMessageRequest(Builder builder) {
        this.requestCount = builder.requestCount;
        this.requestedData = builder.requestedData;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public InventoryVector[] getRequestedData() {
        return requestedData;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(requestCount));
        for(InventoryVector v : requestedData) {
            messageBytes = DataUtil.appendBytes(messageBytes, v.getDataBytes());
        }
        return messageBytes;
    }

    public static class Builder {

        private int requestCount;
        private InventoryVector[] requestedData;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setRequestedTransactions(InventoryVector[] requestedData) {
            this.requestCount = requestedData.length;
            this.requestedData = requestedData;
            return this;
        }

        public DataMessageRequest build() {
            return new DataMessageRequest(this);
        }

    }
}
