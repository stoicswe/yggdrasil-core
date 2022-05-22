package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class TransactionIn implements MessagePayload {

    @NotNull
    private TransactionOutpointPayload txnOutPoint;
    @NotNull
    private int scriptLength;
    @NotNull
    private char[] sigScript;
    @NotNull
    private int sequence;

    private TransactionIn(Builder builder) {
        this.txnOutPoint = builder.txnOutPoint;
        this.scriptLength = builder.scriptLength;
        this.sigScript = builder.sigScript;
        this.sequence = builder.sequence;
    }

    public TransactionOutpointPayload getTxnOutPoint() {
        return txnOutPoint;
    }

    public int getScriptLength() {
        return scriptLength;
    }

    public char[] getSigScript() {
        return sigScript;
    }

    public int getSequence() {
        return sequence;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, txnOutPoint.getDataBytes());
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(scriptLength));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(sigScript));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(sequence));
        return messageBytes;
    }

    public static class Builder {

        private TransactionOutpointPayload txnOutPoint;
        private int scriptLength;
        private char[] sigScript;
        private int sequence;

        public Builder builder() {
            return new Builder();
        }

        public Builder setTxnOutPoint(TransactionOutpointPayload txnOutPoint) {
            this.txnOutPoint = txnOutPoint;
            return this;
        }

        public Builder setSigScript(char[] sigScript) {
            this.scriptLength = sigScript.length;
            this.sigScript = sigScript;
            return this;
        }

        public Builder setSequence(int sequence) {
            this.sequence = sequence;
            return this;
        }

        public TransactionIn build() {
            return new TransactionIn(this);
        }
    }
}
