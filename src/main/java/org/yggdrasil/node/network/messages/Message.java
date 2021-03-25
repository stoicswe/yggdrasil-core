package org.yggdrasil.node.network.messages;

import org.yggdrasil.node.network.messages.enums.NetworkType;
import org.yggdrasil.node.network.messages.enums.RequestType;

import java.math.BigInteger;

/**
 *  The Message object serves as the header for messages sent between nodes. Message
 *  headers contain useful metadata about the payload contained within the message.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class Message {

    private final char[] network;
    private final char[] request;
    private final BigInteger payloadSize;
    private final MessagePayload payload;
    private final char[] checksum;

    private Message(Builder builder) {
        this.network = builder.network;
        this.request = builder.requestType;
        this.payloadSize = builder.payloadSize;
        this.payload = builder.payload;
        this.checksum = builder.checksum;
    }

    public char[] getNetwork() {
        return network;
    }

    public char[] getRequest() {
        return request;
    }

    public BigInteger getPayloadSize() {
        return payloadSize;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public char[] getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        return String.format("Network: [%s], Request; [%s], Checksum: [%s]", String.copyValueOf(network), String.valueOf(request), String.valueOf(checksum));
    }

    public static class Builder {

        private char[] network;
        private char[] requestType;
        private BigInteger payloadSize;
        private MessagePayload payload;
        private char[] checksum;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setNetwork(NetworkType network) {
            this.network = network.getMessageValue();
            return this;
        }

        public Builder setRequestType(RequestType requestType) {
            this.requestType = requestType.getMessageValue();
            return this;
        }

        public Builder setPayloadSize(BigInteger payloadSize) {
            this.payloadSize = payloadSize;
            return this;
        }

        public Builder setMessagePayload(MessagePayload payload) {
            this.payload = payload;
            return this;
        }

        public Builder setChecksum(char[] checksum) {
            this.checksum = checksum;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }
}
