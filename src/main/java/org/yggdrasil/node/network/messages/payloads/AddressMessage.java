package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

/**
 * The Address Message object serves as a container for nodes to share information about
 * other nodes in the network. This allows for a better distributed system.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class AddressMessage implements MessagePayload {

    private final int ipAddressCount;
    private final AddressPayload[] ipAddresses;

    private AddressMessage(Builder builder) {
        this.ipAddressCount = builder.ipAddressCount;
        this.ipAddresses = builder.ipAddresses;
    }

    public int getIpAddressCount() {
        return ipAddressCount;
    }

    public AddressPayload[] getIpAddresses() {
        return ipAddresses;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(ipAddressCount));
        for(AddressPayload ap : ipAddresses) {
            messageBytes = appendBytes(messageBytes, ap.getDataBytes());
        }
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private int ipAddressCount;
        private AddressPayload[] ipAddresses;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setIpAddressCount(int ipAddressCount) {
            this.ipAddressCount = ipAddressCount;
            return this;
        }

        public Builder setIpAddresses(AddressPayload[] ipAddresses) {
            this.ipAddresses = ipAddresses;
            return this;
        }

        public AddressMessage build() {
            return new AddressMessage(this);
        }

    }
}
