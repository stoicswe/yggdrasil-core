package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashArraySerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class NotFoundResponsePayload implements MessagePayload {

    @NotNull
    private int missingCount;
    @NotNull
    @JsonSerialize(using = HashArraySerializer.class)
    private byte[][] missingTransactions;

    private NotFoundResponsePayload(Builder builder) {
        this.missingCount = builder.missingCount;
        this.missingTransactions = builder.missingTransactions;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public byte[][] getMissingTransactions() {
        return missingTransactions;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(missingCount));
        for(byte[] txn : missingTransactions){
            messageBytes = DataUtil.appendBytes(messageBytes, txn);
        }
        return messageBytes;
    }

    public static class Builder {

        private int missingCount;
        private byte[][] missingTransactions;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setMissingTransactions(byte[][] missingTransactions) {
            this.missingCount = missingTransactions.length;
            this.missingTransactions = missingTransactions;
            return this;
        }

        public NotFoundResponsePayload build() {
            return new NotFoundResponsePayload(this);
        }

    }
}
