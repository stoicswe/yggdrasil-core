package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class TransactionWitness implements MessagePayload {

    @NotNull
    private int witnessCompCount;
    @NotNull
    private TransactionWitnessComponent[] witnessComponents;

    private TransactionWitness(Builder builder) {
        this.witnessCompCount = builder.witnessCompCount;
        this.witnessComponents = builder.witnessComponents;
    }

    public int getWitnessCompCount() {
        return witnessCompCount;
    }

    public TransactionWitnessComponent[] getWitnessComponents() {
        return witnessComponents;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(witnessCompCount));
        for(TransactionWitnessComponent c : witnessComponents) {
            messageBytes = DataUtil.appendBytes(messageBytes, c.getDataBytes());
        }
        return messageBytes;
    }

    public static class Builder {

        private int witnessCompCount;
        private TransactionWitnessComponent[] witnessComponents;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setWitnessComponents(TransactionWitnessComponent[] witnessComponents) {
            this.witnessCompCount = witnessComponents.length;
            this.witnessComponents = witnessComponents;
            return this;
        }

        public TransactionWitness build() {
            return new TransactionWitness(this);
        }
    }
}
