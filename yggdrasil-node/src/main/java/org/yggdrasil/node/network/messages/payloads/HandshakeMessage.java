package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.lang.NonNull;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.enums.ServicesType;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * The Handshake Message is used for verifying the connection that has been opened
 * between nodes is functional and able to be used for communication.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
@JsonInclude
public class HandshakeMessage implements MessagePayload {

    @NotNull
    private final int version;
    @NotNull
    private final int services;
    @NotNull
    private final int timestamp;
    @NotNull
    private final char[] receiverAddress;
    @NotNull
    private final int receiverPort;
    @NotNull
    private final char[] senderAddress;
    @NotNull
    private final int senderListeningPort;
    @NotNull
    private final int senderPort;
    @NotNull
    private final char[] userAgent;
    @NotNull
    private final int startHeight;
    @NotNull
    private final int nonce;
    @NotNull
    private final char[] senderIdentifier;

    private HandshakeMessage(Builder builder) {
        this.version = builder.version;
        this.services = builder.services;
        this.timestamp = builder.timestamp;
        this.receiverAddress = builder.receiverAddress;
        this.receiverPort = builder.receiverPort;
        this.senderAddress = builder.senderAddress;
        this.senderListeningPort = builder.senderListeningPort;
        this.senderPort = builder.senderPort;
        this.userAgent = builder.userAgent;
        this.startHeight = builder.startHeight;
        this.nonce = builder.nonce;
        this.senderIdentifier = builder.senderIdentifier;
    }

    public int getVersion() {
        return version;
    }

    public ServicesType getServices() {
        return ServicesType.getByValue(services);
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

    public int getSenderListeningPort() {
        return senderListeningPort;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public char[] getUserAgent() {
        return userAgent;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getNonce() {
        return nonce;
    }

    public char[] getSenderIdentifier() {
        return senderIdentifier;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(version));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(services));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(receiverAddress));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(receiverPort));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(senderAddress));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(senderListeningPort));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(senderPort));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(userAgent));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(startHeight));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(nonce));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(senderIdentifier));
        return messageBytes;
    }

    public static class Builder {

        private int version;
        private int services;
        private int timestamp;
        private char[] receiverAddress;
        private int receiverPort;
        private char[] senderAddress;
        private int senderListeningPort;
        private int senderPort;
        private char[] userAgent;
        private int startHeight;
        private int nonce;
        private char[] senderIdentifier;

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setServices(ServicesType services) {
            this.services = services.getValue();
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

        public Builder setSenderListeningPort(int senderListeningPort) {
            this.senderListeningPort = senderListeningPort;
            return this;
        }

        public Builder setSenderPort(int senderPort) {
            this.senderPort = senderPort;
            return this;
        }

        public Builder setUserAgent(char[] userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder setStartHeight(int startHeight) {
            this.startHeight = startHeight;
            return this;
        }

        public Builder setNonce(int nonce) {
            this.nonce = nonce;
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
