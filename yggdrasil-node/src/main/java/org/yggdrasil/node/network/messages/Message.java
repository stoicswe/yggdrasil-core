package org.yggdrasil.node.network.messages;

import org.yggdrasil.node.network.messages.enums.NetworkType;
import org.yggdrasil.node.network.messages.enums.CommandType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

/**
 *  The Message object serves as the header for messages sent between nodes. Message
 *  headers contain useful metadata about the payload contained within the message.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class Message implements Serializable {

    @NotNull
    private final char[] network;
    @NotNull
    private final char[] command;
    @NotNull
    private final BigInteger payloadSize;
    @NotNull
    private final byte[] checksum;
    @NotNull
    private final MessagePayload payload;

    private Message(Builder builder) {
        this.network = builder.network;
        this.command = builder.requestType;
        this.payloadSize = builder.payloadSize;
        this.payload = builder.payload;
        this.checksum = builder.checksum;
    }

    public char[] getNetwork() {
        return network;
    }

    public CommandType getCommand() {
        return CommandType.getByValue(command);
    }

    public BigInteger getPayloadSize() {
        return payloadSize;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    public boolean compareChecksum(byte[] checkSum) {
        try {
            for(int i = 0; i < checkSum.length; i++){
                if(checkSum[i] != this.checksum[i]){
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("Network: [%s], Request; [%s], Checksum: [%s]", String.copyValueOf(network), String.valueOf(command), String.valueOf(checksum));
    }

    public static class Builder {

        private char[] network;
        private char[] requestType;
        private BigInteger payloadSize;
        private MessagePayload payload;
        private byte[] checksum;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setNetwork(NetworkType network) {
            this.network = network.getMessageValue();
            return this;
        }

        public Builder setRequestType(CommandType commandType) {
            this.requestType = commandType.getValue();
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

        public Builder setChecksum(byte[] checksum) {
            this.checksum = checksum;
            return this;
        }

        public Message build() {
            return new Message(this);
        }

    }
}
