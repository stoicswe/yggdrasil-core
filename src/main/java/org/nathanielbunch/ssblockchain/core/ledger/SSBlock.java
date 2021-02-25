package org.nathanielbunch.ssblockchain.core.ledger;

import org.nathanielbunch.ssblockchain.core.utils.SSHasher;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The SSBlock is the main unit of data in the SSBlockchain. SSBlocks contain
 * an identifier known as the index, a timestamp for data management / sorting,
 * an object that contains transactions, a link to a previous block (previousBlockHash)
 * and the current block hash (blockHash). Blocks can be queried by hash or index.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public final class SSBlock implements Serializable {

    // Make the different fields of the block immutable
    private final UUID index;
    private final LocalDateTime timestamp;
    private final Object transactions;
    private final byte[] previousBlockHash;
    private final byte[] blockHash;

    private SSBlock(BBuilder blockBuilder) throws Exception {
        this.index = blockBuilder.index;
        this.timestamp = blockBuilder.timestamp;
        this.transactions = blockBuilder.transactions;
        this.previousBlockHash = blockBuilder.previousBlock;
        this.blockHash = SSHasher.hash(this);
    }

    public UUID getIndex() {
        return index;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Object getTransactions() {
        return transactions;
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

    /**
     * BBuilder class is the SSBlock builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class BBuilder {

        private UUID index;
        private LocalDateTime timestamp;
        private Object transactions;
        private byte[] previousBlock;

        private BBuilder(){}

        public static BBuilder newSSBlockBuilder(){
            return new BBuilder();
        }

        public BBuilder setTransactions(Object transactions){
            this.transactions = transactions;
            return this;
        }

        public BBuilder setPreviousBlock(byte[] previousBlock){
            this.previousBlock = previousBlock;
            return this;
        }

        public SSBlock build() throws Exception {
            this.index = UUID.randomUUID();
            timestamp = LocalDateTime.now();
            return new SSBlock(this);
        }

    }

}
