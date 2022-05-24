package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.enums.RejectCodeType;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

public class RejectMessagePayload implements MessagePayload {

    @NotNull
    private final char[] message;
    @NotNull
    private final int rejectCode;
    @NotNull
    private final byte[] data;
    
    private RejectMessagePayload(Builder builder) {
        this.message = builder.message;
        this.rejectCode = builder.rejectCode;
        this.data = builder.data;
    }

    public char[] getMessage() {
        return message;
    }

    public RejectCodeType getRejectCode() {
        return RejectCodeType.getByValue(this.rejectCode);
    }

    public byte[] getData() {
        return data;
    }


    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(message));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(rejectCode));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(data));
        return messageBytes;
    }

    public static class Builder {

        private char[] message;
        private int rejectCode;
        private byte[] data;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setMessage(String message) {
            this.message = message.toCharArray();
            return this;
        }

        public Builder setRejectCode(RejectCodeType rejectCode) {
            this.rejectCode = rejectCode.getValue();
            return this;
        }

        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public RejectMessagePayload build() {
            return new RejectMessagePayload(this);
        }

    }
}
