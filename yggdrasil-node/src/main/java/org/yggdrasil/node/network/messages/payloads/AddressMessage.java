package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

/**
 * The Address Message object serves as a container for nodes to share information about
 * other nodes in the network. This allows for a better distributed system.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
@JsonInclude
public class AddressMessage implements MessagePayload {

    @NotNull
    private final int ipAddressCount;
    @NotNull
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
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(ipAddressCount));
        for(AddressPayload ap : ipAddresses) {
            messageBytes = DataUtil.appendBytes(messageBytes, ap.getDataBytes());
        }
        return messageBytes;
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
