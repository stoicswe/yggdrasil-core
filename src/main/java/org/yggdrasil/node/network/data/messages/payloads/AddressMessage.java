package org.yggdrasil.node.network.data.messages.payloads;

import org.yggdrasil.node.network.data.messages.MessagePayload;

/**
 * The Address Message object serves as a container for nodes to share information about
 * other nodes in the network. This allows for a better distributed system.
 *
 * @since 0.0.1
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
