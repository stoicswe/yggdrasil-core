package org.yggdrasil.core.ledger.chain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.core.ledger.LedgerHashableItem;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.transaction.TransactionInput;
import org.yggdrasil.core.ledger.transaction.TransactionOutPoint;
import org.yggdrasil.core.ledger.transaction.TransactionOutput;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.payloads.BlockHeaderPayload;
import org.yggdrasil.node.network.messages.payloads.BlockMessage;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
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
public final class Block implements LedgerHashableItem {

    // Make the different fields of the block immutable
    private final BigInteger blockHeight;
    private final ZonedDateTime timestamp;
    private final List<Transaction> data;
    private final byte[] merkleRoot;
    @JsonSerialize(using = HashSerializer.class)
    private final byte[] previousBlockHash;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] blockHash;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] validator;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] signature;
    private int nonce;

    private Block(Builder blockBuilder) throws NoSuchAlgorithmException {
        this.blockHeight = blockBuilder.blockHeight;
        this.timestamp = blockBuilder.timestamp;
        this.merkleRoot = blockBuilder.merkleRoot;
        this.data = blockBuilder.data;
        this.previousBlockHash = blockBuilder.previousBlock;
        this.nonce = blockBuilder.nonce;
        this.blockHash = CryptoHasher.hash(this);
    }

    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
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
        TransactionOutput txnOut = new TransactionOutput(CryptoHasher.hashByteArray("6ad28d3fda4e10bdc0aaf7112f7818e181defa7e"), BigDecimal.valueOf(50));
        Transaction txn = Transaction.Builder.builder()
                .setTimestamp(DateTimeUtil.fromMessageTimestamp(1625616000))
                .setOriginAddress(null)
                .setDestinationAddress("6ad28d3fda4e10bdc0aaf7112f7818e181defa7e")
                .setTxnInputs(new TransactionInput[]{new TransactionInput((TransactionOutPoint) null, BigDecimal.valueOf(50))})
                .setTxnOutputs(new TransactionOutput[]{txnOut})
                .build();
        byte[] genesisData = new byte[0];
        genesisData = appendBytes(genesisData, txn.getTxnHash());
        genesisData = appendBytes(genesisData, txn.getTxnHash());
        byte[] genesisMerkleRoot = CryptoHasher.dhash(genesisData);
        return new Builder()
                .setBlockHeight(BigInteger.ZERO)
                .setMerkleRoot(genesisMerkleRoot)
                .setPreviousBlock(null)
                .setData(Collections.singletonList(txn))
                .build();
    }

    @JsonIgnore
    @Override
    public byte[] getDataBytes() {
        byte[] blockData = new byte[0];
        blockData = appendBytes(blockData, SerializationUtils.serialize(this.timestamp));
        for(Transaction txn : this.data) {
            blockData = appendBytes(blockData, txn.getTxnHash());
        }
        blockData = appendBytes(blockData, SerializationUtils.serialize(this.previousBlockHash));
        blockData = appendBytes(blockData, SerializationUtils.serialize(this.nonce));
        blockData = appendBytes(blockData, this.signature);
        return blockData;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    /**
     * BBuilder class is the SSBlock builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class Builder {

        private final static Logger logger = LoggerFactory.getLogger(Block.class);

        private BigInteger blockHeight;
        private ZonedDateTime timestamp;
        private byte[] merkleRoot;
        private List<Transaction> data;
        private byte[] previousBlock;
        private byte[] blockHash;
        private int nonce;

        private Builder(){}

        public static Builder newBuilder(){
            return new Builder();
        }

        public Builder setBlockHeight(BigInteger blockHeight) {
            this.blockHeight = blockHeight;
            return this;
        }

        public Builder setMerkleRoot(byte[] merkleRoot) {
            this.merkleRoot = merkleRoot;
            return this;
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
            timestamp = DateTimeUtil.getCurrentTimestamp();
            return new Block(this);
        }

        public Block buildFromBlockHeaderMessage(BlockHeaderPayload blockHeaderPayload) throws NoSuchAlgorithmException {
            this.previousBlock = blockHeaderPayload.getPrevHash();
            // make a thing for the block height
            this.timestamp = DateTimeUtil.fromMessageTimestamp(blockHeaderPayload.getTimestamp());
            this.nonce = blockHeaderPayload.getNonce();
            this.blockHash = blockHeaderPayload.getHash();
            this.data = new ArrayList<>();
            Block blck = new Block(this);
            blck.setBlockHash(this.blockHash);
            return blck;
        }

        public Block buildFromBlockMessage(BlockMessage blockMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            // make a thing for the block height
            this.timestamp = DateTimeUtil.fromMessageTimestamp(blockMessage.getTimestamp());
            this.nonce = blockMessage.getNonce();
            this.blockHash = blockMessage.getBlockHash();
            this.previousBlock = blockMessage.getPreviousBlockHash();
            List<Transaction> data = new ArrayList<>();
            for(TransactionPayload txnPayload : blockMessage.getTxnPayloads()){
                data.add(Transaction.Builder.builder().buildFromMessage(txnPayload));
            }
            this.data = data;
            this.merkleRoot = blockMessage.getMerkleRoot();
            Block blck = new Block(this);
            if(CryptoHasher.isEqualHashes(this.blockHash, blck.blockHash)){
                logger.debug("Locally generated blockhash matched incoming blockhash from payload");
            } else {
                logger.debug("Locally generated blockhash did not match, manually setting blockhaash from payload");
                blck.setBlockHash(this.blockHash);
            }
            blck.setSignature(blockMessage.getSignature());
            return blck;
        }

    }

}
