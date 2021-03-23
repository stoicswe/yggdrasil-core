package org.yggdrasil.node.network.data.messages.payloads;

import java.math.BigInteger;

/**
 * The Address Payload is a message specifically for use with the Address Message.
 * This object holds individual pieces of metadata, as well as the IP Address for
 * a given node.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public class AddressPayload {

    private final int timestamp;
    private final BigInteger services;
    private final char[] ipAddress;
    private final int port;

    private AddressPayload(Builder builder) {
        this.timestamp = builder.timestamp;
        this.services = builder.services;
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public BigInteger getServices() {
        return services;
    }

    public char[] getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public static class Builder {

        private int timestamp;
        private BigInteger services;
        private char[] ipAddress;
        private int port;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setServices(BigInteger services) {
            this.services = services;
            return this;
        }

        public Builder setIpAddress(char[] ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public AddressPayload build() {
            return new AddressPayload(this);
        }

    }
}
