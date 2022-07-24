package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class InventoryMessage implements MessagePayload {

    @NotNull
    private int count;
    @NotNull
    private InventoryVector[] inventory;
    @NotNull
    private byte[] requestChecksum;

    private InventoryMessage(Builder builder) {
        this.count = builder.count;
        this.inventory = builder.inventory;
        this.requestChecksum = builder.requestChecksum;
    }

    public int getCount() {
        return count;
    }

    public InventoryVector[] getInventory() {
        return inventory;
    }

    public byte[] getRequestChecksum() {
        return requestChecksum;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(count));
        for(InventoryVector v : inventory) {
            messageBytes = DataUtil.appendBytes(messageBytes, v.getDataBytes());
        }
        messageBytes = DataUtil.appendBytes(messageBytes, requestChecksum);
        return messageBytes;
    }

    public static class Builder {

        private int count;
        private InventoryVector[] inventory;
        private byte[] requestChecksum;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setInventory(InventoryVector[] inventory) {
            this.count = inventory.length;
            this.inventory = inventory;
            return this;
        }

        public Builder setRequestChecksum(byte[] requestChecksum) {
            this.requestChecksum = requestChecksum;
            return this;
        }

        public InventoryMessage build() {
            return new InventoryMessage(this);
        }

    }

}
