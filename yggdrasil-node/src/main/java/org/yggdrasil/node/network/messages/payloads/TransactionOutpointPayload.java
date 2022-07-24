package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class TransactionOutpointPayload implements MessagePayload {

    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private byte[] hash;
    @NotNull
    private int index;

    private TransactionOutpointPayload(Builder builder) {
        this.hash = builder.hash;
        this.index = builder.index;
    }

    public byte[] getHash() {
        return hash;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, hash);
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(index));
        return messageBytes;
    }

    public static class Builder {
        private byte[] hash;
        private int index;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setHash(byte[] hash) {
            this.hash = hash;
            return this;
        }

        public Builder setIndex(int index) {
            this.index = index;
            return this;
        }

        public TransactionOutpointPayload build() {
            return new TransactionOutpointPayload(this);
        }

    }
}
