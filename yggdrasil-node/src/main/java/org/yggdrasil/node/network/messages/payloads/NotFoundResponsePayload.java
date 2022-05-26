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
    private InventoryVector[] missingItems;

    private NotFoundResponsePayload(Builder builder) {
        this.missingCount = builder.missingCount;
        this.missingItems = builder.missingItems;
    }

    public int getMissingCount() {
        return missingCount;
    }

    public InventoryVector[] getMissingItems() {
        return missingItems;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(missingCount));
        for(InventoryVector item : missingItems){
            messageBytes = DataUtil.appendBytes(messageBytes, item.getDataBytes());
        }
        return messageBytes;
    }

    public static class Builder {

        private int missingCount;
        private InventoryVector[] missingItems;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setMissingItems(InventoryVector[] missingItems) {
            this.missingCount = missingItems.length;
            this.missingItems = missingItems;
            return this;
        }

        public NotFoundResponsePayload build() {
            return new NotFoundResponsePayload(this);
        }

    }
}
