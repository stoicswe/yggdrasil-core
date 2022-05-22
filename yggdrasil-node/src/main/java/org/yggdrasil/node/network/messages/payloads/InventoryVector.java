package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.enums.InventoryType;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonSerialize
public class InventoryVector implements MessagePayload {

    @NotNull
    private final int type;
    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] hash;

    private InventoryVector(Builder builder) {
        this.type = builder.type;
        this.hash = builder.hash;
    }

    public InventoryType getType() {
        return InventoryType.getByValue(type);
    }

    public byte[] getHash() {
        return hash;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(type));
        messageBytes = DataUtil.appendBytes(messageBytes, hash);
        return messageBytes;
    }

    public static class Builder {

        private int type;
        private byte[] hash;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setType(InventoryType type) {
            this.type = type.getValue();
            return this;
        }

        public InventoryVector build() {
            return new InventoryVector(this);
        }

    }
}
