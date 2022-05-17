package org.yggdrasil.core.ledger.chain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.core.ledger.LedgerHashableItem;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.messages.payloads.BlockMessage;

import java.time.ZonedDateTime;

/**
 * The block header is used to identify blocks. The hash of this header is what
 * is used for record keeping.
 *
 */
@JsonInclude
public class BlockHeader implements LedgerHashableItem {

    // The version of the software
    private final int version;
    // The previous block's hash
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] previousBlockHash;
    // The merkle root of the txns
    private byte[] merkleRoot;
    // The real time stamp of the block's creation, stored in UTC in order to allow
    // for the block height to be directly correlated with the timestamp.
    private ZonedDateTime time;
    // The difficulty of the work done
    private final int diff;
    // The nonce for PoW
    private int nonce;

    // The constructor here is private in order to further drive
    // home the idea of immutability for all parts of data.
    private BlockHeader(Builder builder) {
        this.version = builder.version;
        this.previousBlockHash = builder.previousBlockHash;
        this.merkleRoot = builder.merkleRoot;
        this.time = builder.time;
        this.diff = builder.diff;
        this.nonce = builder.nonce;
    }

    /**
     * Returns the version of the software from the block header.
     *
     * @return softwareVersion
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the previous block hash that this header is connected to
     * in the chain.
     *
     * @return previousBlockHash
     */
    public byte[] getPreviousBlockHash() {
        return previousBlockHash;
    }

    /**
     * Returns the merkleRoot of the block this header belongs to.
     *
     * @return merkleRoot
     */
    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * Set the merkleRoot of the block. This should only occur during the
     * mining process as PoW.
     *
     * @param merkleRoot
     */
    public void setMerkleRoot(byte[] merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    /**
     * Returns the timestamp of the block that was created.
     *
     * @return timestamp
     */
    public ZonedDateTime getTime() {
        return time;
    }

    /**
     * Returns the timestamp of the block that was created in epoch seconds.
     *
     * @return timestamp
     */
    public long getEpochTime() {
        return DateTimeUtil.toEpochSecondTimeStamp(time);
    }

    /**
     * Set the timestamp of the block as it is created. This should only
     * happen during the mining process.
     *
     * @param time
     */
    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    /**
     * Returns the current difficulty of the block that was calculated during PoW.
     *
     * @return difficulty
     */
    public int getDiff() {
        return diff;
    }

    /**
     * Returns the nonce that was used to meet/beat the difficulty.
     *
     * @return nonce
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * Set the nonce for the block to try to beat the difficulty, given the
     * data in the block.
     *
     * @param nonce
     */
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    /**
     * Gets the byte array of all data in the block used primarily for creating the block
     * hash.
     *
     * @return blockHeaderBytes
     */
    @Override
    public byte[] getDataBytes() {
        byte[] blockHeaderData = new byte[0];
        blockHeaderData = appendBytes(blockHeaderData, SerializationUtils.serialize(this.version));
        blockHeaderData = appendBytes(blockHeaderData, this.previousBlockHash);
        blockHeaderData = appendBytes(blockHeaderData, this.merkleRoot);
        blockHeaderData = appendBytes(blockHeaderData, SerializationUtils.serialize(DateTimeUtil.toEpochSecondTimeStamp(time)));
        blockHeaderData = appendBytes(blockHeaderData, SerializationUtils.serialize(this.diff));
        blockHeaderData = appendBytes(blockHeaderData, SerializationUtils.serialize(nonce));
        return blockHeaderData;
    }

    /**
     * Internal use only function. Used for facilitating the appending of the bytes
     * of data that are contained in the block.
     *
     * @param base
     * @param extension
     * @return
     */
    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    /**
     * Builder class for facilitating the instantiation of block headers. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class Builder {

        private static final Logger logger = LoggerFactory.getLogger(BlockHeader.class);

        private int version;
        private byte[] previousBlockHash;
        private byte[] merkleRoot;
        private ZonedDateTime time;
        private int diff;
        private int nonce;

        public static Builder builder() {
            return new Builder();
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setPreviousBlockHash(byte[] previousBlock) {
            this.previousBlockHash = previousBlock;
            return this;
        }

        public Builder setMerkleRoot(byte[] merkleRoot) {
            this.merkleRoot = merkleRoot;
            return this;
        }

        public Builder setTime(ZonedDateTime time){
            this.time = time;
            return this;
        }

        public Builder setDiff(int diff) {
            this.diff = diff;
            return this;
        }

        public Builder setNonce(int nonce) {
            this.nonce = nonce;
            return this;
        }

        public BlockHeader build() {
            return new BlockHeader(this);
        }

        /**
         * Builds a block object from the
         *
         * @param message
         * @return blockHeader
         */
        public BlockHeader buildFromMessage(BlockHeaderPayload message) {
            this.version = message.getVersion();
            this.previousBlockHash = message.getPrevBlock();
            this.merkleRoot = message.getMerkleRoot();
            this.time = DateTimeUtil.fromMessageTimestamp(message.getTimestamp());
            this.diff = message.getDiff();
            this.nonce = message.getNonce();
            return new BlockHeader(this);
        }

        public BlockHeader buildFromMessage(BlockMessage message) {
            this.version = message.getVersion();
            this.previousBlockHash = message.getPrevBlock();
            this.merkleRoot = message.getMerkleRoot();
            this.time = DateTimeUtil.fromMessageTimestamp(message.getTimestamp());
            this.diff = message.getDiff();
            this.nonce = message.getNonce();
            return new BlockHeader(this);
        }

    }
}
