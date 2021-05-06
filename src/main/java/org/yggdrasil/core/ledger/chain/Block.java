package org.yggdrasil.core.ledger.chain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * The Block is the main unit of data in Yggdrasil. Blocks contain
 * an identifier known as the index, a timestamp for data management / sorting,
 * an object that contains transactions, a link to a previous block (previousBlockHash)
 * and the current block hash (blockHash). Blocks can be queried by hash or index.
 *
 * @since 0.0.2
 * @author nathanielbunch
 */
@JsonInclude
@JsonIgnoreProperties(value = "nonce")
public final class Block implements Serializable {

    // Make the different fields of the block immutable
    private final UUID index;
    private final ZonedDateTime timestamp;
    private final List<Transaction> data;
    private final byte[] previousBlockHash;
    private byte[] blockHash;
    private byte[] validator;
    private byte[] signature;
    private int nonce;

    private Block(Builder blockBuilder) throws NoSuchAlgorithmException {
        this.index = blockBuilder.index;
        this.timestamp = blockBuilder.timestamp;
        this.data = blockBuilder.data;
        this.previousBlockHash = blockBuilder.previousBlock;
        this.nonce = blockBuilder.nonce;
        this.blockHash = CryptoHasher.hash(this);
    }

    public UUID getIndex() {
        return index;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getData() {
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

    public Optional<Transaction> getTransaction(byte[] txnHash) {
        return this.data.stream().filter(ftxn -> ftxn.compareTxnHash(txnHash)).findFirst();
    }

    public boolean compareBlockHash(byte[] blockHash) {
        try {
            for (int i = 0; i < blockHash.length; i++) {
                if (blockHash[i] != this.blockHash[i]) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return CryptoHasher.humanReadableHash(blockHash);
    }

    public static Block genesis() throws Exception {
        return new Builder()
                .setPreviousBlock(null)
                .setData(Collections.singletonList(Transaction.Builder.Builder()
                        .setOrigin(new byte[0])
                        .setDestination(new byte[0])
                        .setNote("'Stay thinking different' - Steve Jobs")
                        .setSignature(new byte[0])
                        .setValue(BigDecimal.valueOf(10, 7))
                        .build()))
                .build();
    }

    /**
     * BBuilder class is the SSBlock builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class Builder {

        private UUID index;
        private ZonedDateTime timestamp;
        private List<Transaction> data;
        private byte[] previousBlock;
        private byte[] blockHash;
        private int nonce;

        private Builder(){}

        public static Builder newBuilder(){
            return new Builder();
        }

        public Builder setData(List<Transaction> data){
            this.data = data;
            return this;
        }

        public Builder setPreviousBlock(byte[] previousBlock){
            this.previousBlock = previousBlock;
            return this;
        }

        public Block build() throws Exception {
            this.index = UUID.randomUUID();
            timestamp = DateTimeUtil.getCurrentTimestamp();
            return new Block(this);
        }

        public Block buildFromBlockHeaderMessage(BlockHeaderPayload blockHeaderPayload) throws NoSuchAlgorithmException {
            this.previousBlock = blockHeaderPayload.getPrevHash();
            this.index = UUID.fromString(String.valueOf(blockHeaderPayload.getIndex()));
            this.timestamp = DateTimeUtil.fromMessageTimestamp(blockHeaderPayload.getTimestamp());
            this.nonce = blockHeaderPayload.getNonce();
            this.blockHash = blockHeaderPayload.getHash();
            this.data = new ArrayList<>();
            Block blck = new Block(this);
            blck.setBlockHash(this.blockHash);
            return blck;
        }

    }

}
