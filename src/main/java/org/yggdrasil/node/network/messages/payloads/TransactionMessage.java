package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

import java.math.BigDecimal;

/**
 * The Transaction Message contains the full information of a transaction.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class TransactionMessage implements MessagePayload {

    private final char[] index;
    private final int timestamp;
    private final char[] originAddress;
    private final char[] destinationAddress;
    private final BigDecimal value;
    private final char[] transactionHash;
    private final char[] blockHash;

    private TransactionMessage(Builder builder){
        this.index = builder.index;
        this.timestamp = builder.timestamp;
        this.originAddress = builder.originAddress;
        this.destinationAddress = builder.destinationAddress;
        this.value = builder.value;
        this.transactionHash = builder.transactionHash;
        this.blockHash = builder.blockHash;
    }

    public char[] getIndex() {
        return index;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public char[] getOriginAddress() {
        return originAddress;
    }

    public char[] getDestinationAddress() {
        return destinationAddress;
    }

    public BigDecimal getValue() {
        return value;
    }

    public char[] getTransactionHash() {
        return transactionHash;
    }

    public char[] getBlockHash() {
        return blockHash;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(index));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(timestamp));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(originAddress));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(destinationAddress));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(value));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(transactionHash));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(blockHash));
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private char[] index;
        private int timestamp;
        private char[] originAddress;
        private char[] destinationAddress;
        private BigDecimal value;
        private char[] transactionHash;
        private char[] blockHash;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setIndex(char[] index) {
            this.index = index;
            return this;
        }

        public Builder setTimestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setOriginAddress(char[] originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public Builder setDestinationAddress(char[] destinationAddress) {
            this.destinationAddress = destinationAddress;
            return this;
        }

        public Builder setValue(BigDecimal value) {
            this.value = value;
            return this;
        }

        public Builder setTransactionHash(char[] transactionHash) {
            this.transactionHash = transactionHash;
            return this;
        }

        public Builder setBlockHash(char[] blockHash) {
            this.blockHash = blockHash;
            return this;
        }

    }
}
