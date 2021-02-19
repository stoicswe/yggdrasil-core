package org.nathanielbunch.ssblockchain.core;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public final class SSBlock implements Serializable {

    // Make the different fields of the block immutable
    private final Integer index;
    private final LocalDateTime timestamp;
    private final Object data;
    private final byte[] previousBlockHash;
    private final byte[] blockHash;

    private SSBlock(BBuilder blockBuilder) throws Exception {
        this.index = blockBuilder.index;
        this.timestamp = blockBuilder.timestamp;
        this.data = blockBuilder.data;
        this.previousBlockHash = blockBuilder.previousBlock;
        this.blockHash = hashSSBlock(this);
    }

    public Integer getIndex() {
        return index;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Object getData() {
        return data;
    }

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    @Override
    public String toString() {
        StringBuilder hexString = new StringBuilder(2 * blockHash.length);
        for (int i = 0; i < blockHash.length; i++) {
            String hex = Integer.toHexString(0xff & blockHash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private byte[] hashSSBlock(SSBlock ssBlock) throws Exception {
        return MessageDigest.getInstance("SHA3-512").digest(SerializationUtils.serialize(ssBlock));
    }

    public static class BBuilder {

        // Unique index for each block
        private static int count = 0;

        // Block properties
        private Integer index = 0;
        private LocalDateTime timestamp;
        private Object data;
        private byte[] previousBlock;

        private BBuilder(){}

        public static BBuilder newSSBlockBuilder(){
            return new BBuilder();
        }

        public BBuilder setData(Object data){
            this.data = data;
            return this;
        }

        public BBuilder setPreviousBlock(byte[] previousBlock){
            this.previousBlock = previousBlock;
            return this;
        }

        // Build a block
        public SSBlock build() throws Exception {
            this.index = count++;
            timestamp = LocalDateTime.now();
            return new SSBlock(this);
        }

    }

}
