package org.nathanielbunch.ssblockchain.core.ledger;

import org.nathanielbunch.ssblockchain.core.utils.SSHasher;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public final class SSBlock implements Serializable {

    // Make the different fields of the block immutable
    private final UUID index;
    private final LocalDateTime timestamp;
    private final Object data;
    private final byte[] previousBlockHash;
    private final byte[] blockHash;

    private SSBlock(BBuilder blockBuilder) throws Exception {
        this.index = blockBuilder.index;
        this.timestamp = blockBuilder.timestamp;
        this.data = blockBuilder.data;
        this.previousBlockHash = blockBuilder.previousBlock;
        this.blockHash = SSHasher.hash(this);
    }

    public UUID getIndex() {
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
        return SSHasher.humanReadableHash(blockHash);
    }

    public static class BBuilder {
        // Block properties
        private UUID index;
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
            this.index = UUID.randomUUID();
            timestamp = LocalDateTime.now();
            return new SSBlock(this);
        }

    }

}
