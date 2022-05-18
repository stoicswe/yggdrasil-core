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
public final class Block {

    // The block header that describes the identification of this block. The
    // block's identification is simply a hash of this header data.
    private final BlockHeader header;
    // The height of the block in the chain. The block height will provide
    // a sense of "time" or indexed record of the general era when a block was
    // created.
    private BigInteger blockHeight;
    // The list of txns that are stored in the block after being mined. Once txns are confirmed
    // and the block stored, this list becomes immutable...a permanent record in the blockchain.
    private final List<Transaction> data;
    // The current block's hash for identifying itself. This is used in the lambda expressions for
    // finding the block being queried. The blockHash is the hash of the blockHeader.
    @JsonSerialize(using = HashSerializer.class)
    private byte[] blockHash;

    // The constructor here is private. The reason being that across the code-base the general ideology is to
    // use the "builder" methodology. This is to further enforce the idea of immutability of data.
    private Block(Builder builder) throws NoSuchAlgorithmException {
        this.header = builder.header;
        this.blockHeight = builder.blockHeight;
        this.data = builder.data;
        this.blockHash = CryptoHasher.hash(this.header);
    }

    /**
     * Returns the block header for this block.
     *
     * @return blockHeader
     */
    public BlockHeader getHeader() {
        return header;
    }

    /**
     * Sets the blockheight at the point a
     * block has been created.
     *
     * @return blockHeight
     */
    protected void setBlockHeight(BigInteger blockHeight) {
        this.blockHeight = blockHeight;
    }

    /**
     * Returns a BigDecimal value that indicates the blockheight at the point a
     * block has been created.
     *
     * @return blockHeight
     */
    public BigInteger getBlockHeight() {
        return blockHeight;
    }

    /**
     * Returns the array of txns that are contained in the block.
     *
     * @return data
     */
    public List<Transaction> getData() {
        return data;
    }

    /**
     * Returns this current block's hash.
     *
     * @return blockHash
     */
    public byte[] getBlockHash() {
        return blockHash;
    }

    /**
     * Returns a specific txn from the block, provided the txn hash for querying.
     *
     * @param txnHash
     * @return transaction
     */
    public Optional<Transaction> getTransaction(byte[] txnHash) {
        return this.data.stream().filter(ftxn -> ftxn.compareTxnHash(txnHash)).findFirst();
    }

    public int getTxnCount() {
        if(data != null) return this.data.size();
        return -1;
    }

    /**
     * A comparator function for comparing a provided block hash with this block's hash.
     *
     * @param blockHash
     * @return isSameBlockHash
     */
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

    /**
     * When printing to the terminal, we do not need all the data printed, typically, so
     * just print the block hash...converted to human-readable form of course. :-)
     *
     * @return
     */
    @Override
    public String toString() {
        return CryptoHasher.humanReadableHash(blockHash);
    }

    /**
     * Creates the genesis block for use in the chain with a set block hash and a single coinbase txn.
     *
     * @return
     * @throws Exception
     */
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
                .setBlockHeight(BigInteger.ONE)
                .setData(Collections.singletonList(txn))
                .build();
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
     * Builder class for facilitating the instantiation of blocks. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class Builder {

        private final static Logger logger = LoggerFactory.getLogger(Block.class);

        private BlockHeader header;
        private BigInteger blockHeight;
        private List<Transaction> data;
        private byte[] blockHash;

        private Builder(){}

        public static Builder newBuilder(){
            return new Builder();
        }

        public Builder setBlockHeader(BlockHeader blockHeader) {
            this.header = blockHeader;
            return this;
        }

        public Builder setBlockHeight(BigInteger blockHeight) {
            this.blockHeight = blockHeight;
            return this;
        }

        public Builder setData(List<Transaction> data){
            this.data = data;
            return this;
        }

        public Block build() throws NoSuchAlgorithmException {
            return new Block(this);
        }

        /**
         * When a block header message is received, convert that message back into a
         * block in memory so it can be used properly. This will instantiate a skeleton
         * block...one that does not have any txns in it for the sake of identification
         * data only.
         *
         * @param blockHeaderPayload
         * @return block
         * @throws NoSuchAlgorithmException
         */
        public Block buildFromBlockHeaderMessage(BlockHeaderPayload blockHeaderPayload) throws NoSuchAlgorithmException {
            this.header = BlockHeader.Builder.builder().buildFromMessage(blockHeaderPayload);
            this.blockHash = CryptoHasher.hash(this.header);
            this.data = new ArrayList<>();
            return new Block(this);
        }

        /**
         * When a full block message is received, convert that message back into a block
         * in memory so it can be used properly. This will result in instantiating a full
         * block, with txns for archival storage.
         *
         * @param blockMessage
         * @return block
         * @throws NoSuchAlgorithmException
         * @throws InvalidKeySpecException
         * @throws NoSuchProviderException
         */
        public Block buildFromBlockMessage(BlockMessage blockMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            this.header = BlockHeader.Builder.builder().buildFromMessage(blockMessage);
            this.blockHash = CryptoHasher.hash(this.header);
            List<Transaction> data = new ArrayList<>();
            for(TransactionPayload txnPayload : blockMessage.getTxnPayloads()){
                data.add(Transaction.Builder.builder().buildFromMessage(txnPayload));
            }
            this.data = data;
            return new Block(this);
        }

    }

}
