package org.yggdrasil.node.network.peer;

import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.payloads.AddressPayload;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PeerRecord implements Serializable {

    private final UUID nodeIdentifier;
    private ZonedDateTime timeStamp;
    private final BigInteger supportedServices;
    private final String ipAddress;
    private final int port;

    private PeerRecord(Builder builder) {
        this.nodeIdentifier = builder.nodeIdentifier;
        this.timeStamp = builder.timeStamp;
        this.supportedServices = builder.supportedServices;
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
    }

    public UUID getNodeIdentifier() {
        return nodeIdentifier;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public BigInteger getSupportedServices() {
        return supportedServices;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public AddressPayload toAddressPayload() {
        return AddressPayload.Builder.newBuilder()
                .setNodeIdentifier(this.nodeIdentifier.toString().toCharArray())
                .setTimestamp((int) this.timeStamp.toEpochSecond())
                .setServices(this.supportedServices)
                .setIpAddress(this.ipAddress.toCharArray())
                .setPort(this.port)
                .build();
    }

    public static class Builder {

        private UUID nodeIdentifier;
        private ZonedDateTime timeStamp;
        private BigInteger supportedServices;
        private String ipAddress;
        private int port;

        private Builder(){}

        public static Builder newBuilder(){
            return new Builder();
        }

        public Builder setNodeIdentifier(UUID nodeIdentifier) {
            this.nodeIdentifier = nodeIdentifier;
            return this;
        }

        public Builder setNodeIdentifier(String nodeIdentifier) {
            this.nodeIdentifier = UUID.fromString(nodeIdentifier);
            return this;
        }

        public Builder setTimeStamp(ZonedDateTime timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder setSupportedServices(BigInteger supportedServices) {
            this.supportedServices = supportedServices;
            return this;
        }

        public Builder setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public PeerRecord build() {
            return new PeerRecord(this);
        }

        public PeerRecord buildFromAddressPayload(AddressPayload addressPayload) {
            this.nodeIdentifier = UUID.fromString(String.valueOf(addressPayload.getNodeIdentifier()));
            this.timeStamp = DateTimeUtil.fromMessageTimestamp(addressPayload.getTimestamp());
            this.supportedServices = addressPayload.getServices();
            this.ipAddress = String.valueOf(addressPayload.getIpAddress());
            this.port = addressPayload.getPort();
            return new PeerRecord(this);
        }
    }
}
