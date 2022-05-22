package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@JsonInclude
public class TransactionOut implements MessagePayload {

    @NotNull
    private BigInteger value;
    @NotNull
    private int scriptLength;
    @NotNull
    private char[] script;

    private TransactionOut(Builder builder) {
        this.value = builder.value;
        this.scriptLength = builder.scriptLength;
        this.script = builder.script;
    }

    public BigInteger getValue() {
        return value;
    }

    public int getScriptLength() {
        return scriptLength;
    }

    public char[] getScript() {
        return script;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(value));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(scriptLength));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(script));
        return messageBytes;
    }

    public static class Builder {

        private BigInteger value;
        private int scriptLength;
        private char[] script;

        public Builder builder() {
            return new Builder();
        }

        public Builder setValue(BigInteger value) {
            this.value = value;
            return this;
        }

        public Builder setScript(char[] script) {
            this.scriptLength = script.length;
            this.script = script;
            return this;
        }

    }
}
