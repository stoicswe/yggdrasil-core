package org.yggdrasil.core.ledger.chain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * The Block is the main unit of data in Yggdrasil. Blocks contain
 * an identifier known as the index, a timestamp for data management / sorting,
 * an object that contains transactions, a link to a previous block (previousBlockHash)
 * and the current block hash (blockHash). Blocks can be queried by hash or index.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
@JsonInclude
@JsonIgnoreProperties(value = "nonce")
public final class Block implements Serializable {

    // Make the different fields of the block immutable
    private final UUID index;
    private final ZonedDateTime timestamp;
    private final Object data;
    private final byte[] previousBlockHash;
    private byte[] blockHash;
    private byte[] validator;
    private byte[] signature;
    private int nonce;

    private Block(BBuilder blockBuilder) throws Exception {
        this.index = blockBuilder.index;
        this.timestamp = blockBuilder.timestamp;
        this.data = blockBuilder.data;
        this.previousBlockHash = blockBuilder.previousBlock;
        this.blockHash = CryptoHasher.hash(this);
    }

    public UUID getIndex() {
        return index;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public Object getData() {
        return data;
    }

    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setBlockHash(byte[] blockHash) {
        this.blockHash = blockHash;
    }

    public byte[] getBlockHash() {
        return blockHash;
    }

    public byte[] getValidator() {
        return validator;
    }

    public void setValidator(byte[] validator) {
        this.validator = validator;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public void incrementNonce() {
        this.nonce++;
    }

    public int getNonce() {
        return nonce;
    }

    @Override
    public String toString() {
        return CryptoHasher.humanReadableHash(blockHash);
    }

    public static Block genesis() throws Exception {
        return new Block.BBuilder()
                .setPreviousBlock(null)
                .setData("'Think Different' - Steve Jobs")
                .build();
    }

    /**
     * BBuilder class is the SSBlock builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class BBuilder {

        private UUID index;
        private ZonedDateTime timestamp;
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

        public Block build() throws Exception {
            this.index = UUID.randomUUID();
            timestamp = DateTimeUtil.getCurrentTimestamp();
            return new Block(this);
        }

    }

}
