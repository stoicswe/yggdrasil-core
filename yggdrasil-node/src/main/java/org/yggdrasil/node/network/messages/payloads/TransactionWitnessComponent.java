package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class TransactionWitnessComponent implements MessagePayload {

    @NotNull
    private int componentLength;
    @NotNull
    @JsonIgnore
    private byte[] component;

    private TransactionWitnessComponent(Builder builder) {
        this.componentLength = builder.componentLength;
        this.component = builder.component;
    }

    public int getComponentLength() {
        return componentLength;
    }

    public byte[] getComponent() {
        return component;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(componentLength));
        messageBytes = DataUtil.appendBytes(messageBytes, component);
        return messageBytes;
    }

    public static class Builder {

        private int componentLength;
        private byte[] component;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setComponent(byte[] component) {
            this.componentLength = component.length;
            this.component = component;
            return this;
        }

        public TransactionWitnessComponent build() {
            return new TransactionWitnessComponent(this);
        }

    }


}
