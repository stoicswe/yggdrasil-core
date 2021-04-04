package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.lang.NonNull;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * The Handshake Message is used for verifying the connection that has been opened
 * between nodes is functional and able to be used for communication.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class HandshakeMessage implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    private final BigInteger services;
    @NotNull
    private final int timestamp;
    @NotNull
    private final char[] receiverAddress;
    @NotNull
    private final int receiverPort;
    @NotNull
    private final char[] senderAddress;
    @NotNull
    private final int senderPort;
    @NotNull
    private final char[] senderIdentifier;

    private HandshakeMessage(Builder builder) {
        this.version = builder.version;
        this.services = builder.services;
        this.timestamp = builder.timestamp;
        this.receiverAddress = builder.receiverAddress;
        this.receiverPort = builder.receiverPort;
        this.senderAddress = builder.senderAddress;
        this.senderPort = builder.senderPort;
        this.senderIdentifier = builder.senderIdentifier;
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

    public char[] getSenderIdentifier() {
        return senderIdentifier;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(services));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(receiverAddress));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(receiverPort));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(senderAddress));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(senderIdentifier));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(senderPort));
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private int version;
        private BigInteger services;
        private int timestamp;
        private char[] receiverAddress;
        private int receiverPort;
        private char[] senderAddress;
        private int senderPort;
        private char[] senderIdentifier;

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

        public Builder setSenderIdentifier(char[] senderIdentifier) {
            this.senderIdentifier = senderIdentifier;
            return this;
        }

        public HandshakeMessage build() {
            return new HandshakeMessage(this);
        }

    }
}
