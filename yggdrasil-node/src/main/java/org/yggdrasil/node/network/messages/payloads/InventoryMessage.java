package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.enums.InventoryType;

import javax.validation.constraints.NotNull;

@JsonInclude
public class InventoryMessage implements MessagePayload {

    @NotNull
    private final int type;
    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] hash;

    private InventoryMessage(Builder builder) {
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
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(type));
        messageBytes = appendBytes(messageBytes, hash);
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
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

        public InventoryMessage build() {
            return new InventoryMessage(this);
        }

    }

}
