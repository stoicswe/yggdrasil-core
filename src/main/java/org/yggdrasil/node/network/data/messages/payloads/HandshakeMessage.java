package org.yggdrasil.node.network.data.messages.payloads;

import org.yggdrasil.node.network.data.messages.MessagePayload;

import java.math.BigInteger;

/**
 * The Handshake Message is used for verifying the connection that has been opened
 * between nodes is functional and able to be used for communication.
 */
public class HandshakeMessage implements MessagePayload {

    private final int version;
    private final BigInteger services;
    private final int timestamp;
    private final char[] receiverAddress;
    private final int receiverPort;
    private final char[] senderAddress;
    private final int senderPort;

    private HandshakeMessage(Builder builder) {
        this.version = builder.version;
        this.services = builder.services;
        this.timestamp = builder.timestamp;
        this.receiverAddress = builder.receiverAddress;
        this.receiverPort = builder.receiverPort;
        this.senderAddress = builder.senderAddress;
        this.senderPort = builder.senderPort;
    }

    public int getVersion() {
        return version;
    }

    public BigInteger getServices() {
        return services;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public char[] getReceiverAddress() {
        return receiverAddress;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public char[] getSenderAddress() {
        return senderAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public static class Builder {

        private int version;
        private BigInteger services;
        private int timestamp;
        private char[] receiverAddress;
        private int receiverPort;
        private char[] senderAddress;
        private int senderPort;

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setServices(BigInteger services) {
            this.services = services;
            return this;
        }

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setReceiverAddress(char[] receiverAddress) {
            this.receiverAddress = receiverAddress;
            return this;
        }

        public Builder setReceiverPort(int receiverPort) {
            this.receiverPort = receiverPort;
            return this;
        }

        public Builder setSenderAddress(char[] senderAddress) {
            this.senderAddress = senderAddress;
            return this;
        }

        public Builder setSenderPort(int senderPort) {
            this.senderPort = senderPort;
            return this;
        }

        public HandshakeMessage build() {
            return new HandshakeMessage(this);
        }

    }
}
