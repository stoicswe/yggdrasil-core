package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

/**
 * The acknowledge message is for responding to a node
 * to indicate that it received a message from an originating node.
 *
 * @since 0.0.13
 * @author nathanielbunch
 */
public class AcknowledgeMessage implements MessagePayload {

    private final byte[] acknowledgeChecksum;

    private AcknowledgeMessage(Builder builder){
        this.acknowledgeChecksum = builder.acknowledgeChecksum;
    }

    public byte[] getAcknowledgeChecksum() {
        return acknowledgeChecksum;
    }

    @Override
    public byte[] getDataBytes() {
        return SerializationUtils.serialize(acknowledgeChecksum);
    }

    public static class Builder {

        private byte[] acknowledgeChecksum;

        private Builder() {}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setAcknowledgeChecksum(byte[] acknowledgeChecksum) {
            this.acknowledgeChecksum = acknowledgeChecksum;
            return this;
        }

        public AcknowledgeMessage build() {
            return new AcknowledgeMessage(this);
        }

    }
}
