package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * The Transaction Message contains the full information of a transaction.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class TransactionMessage implements MessagePayload {

    @NotNull
    private final char[] index;
    @NotNull
    private final int timestamp;
    @NotNull
    private final byte[] originAddress;
    @NotNull
    private final byte[] destinationAddress;
    @NotNull
    private final BigDecimal value;
    @NotNull
    private final byte[] transactionHash;
    @NotNull
    private final byte[] signature;
    @NotNull
    private final byte[] blockHash;

    private TransactionMessage(Builder builder){
        this.index = builder.index;
        this.timestamp = builder.timestamp;
        this.originAddress = builder.originAddress;
        this.destinationAddress = builder.destinationAddress;
        this.value = builder.value;
        this.transactionHash = builder.transactionHash;
        this.signature = builder.signature;
        this.blockHash = builder.blockHash;
    }

    public char[] getIndex() {
        return index;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public byte[] getOriginAddress() {
        return originAddress;
    }

    public byte[] getDestinationAddress() {
        return destinationAddress;
    }

    public BigDecimal getValue() {
        return value;
    }

    public byte[] getTransactionHash() {
        return transactionHash;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public byte[] getBlockHash() {
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
        messageBytes = appendBytes(messageBytes, transactionHash);
        messageBytes = appendBytes(messageBytes, signature);
        messageBytes = appendBytes(messageBytes, blockHash);
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private char[] index;
        private int timestamp;
        private byte[] originAddress;
        private byte[] destinationAddress;
        private BigDecimal value;
        private byte[] transactionHash;
        private byte[] signature;
        private byte[] blockHash;

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

        public Builder setOriginAddress(byte[] originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public Builder setDestinationAddress(byte[] destinationAddress) {
            this.destinationAddress = destinationAddress;
            return this;
        }

        public Builder setValue(BigDecimal value) {
            this.value = value;
            return this;
        }

        public Builder setTransactionHash(byte[] transactionHash) {
            this.transactionHash = transactionHash;
            return this;
        }

        public Builder setSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }

        public Builder setBlockHash(byte[] blockHash) {
            this.blockHash = blockHash;
            return this;
        }

        public TransactionMessage build() {
            return new TransactionMessage(this);
        }

    }
}
