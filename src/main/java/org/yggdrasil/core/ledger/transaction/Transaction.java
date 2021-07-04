package org.yggdrasil.core.ledger.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.ledger.LedgerHashableItem;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionPayload;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.ZonedDateTime;

/**
 * Every Block is made of n number of Transactions. Transactions contain
 * information used for identifying a transaction (index), a timestamp for
 * sorting and managing transactions in a block, an origin address, a
 * destination address, an amount of coin transmitted, a transaction note, and
 * the identifying transaction hash. Transactions can be queried for by their
 * hash or their index and timestamp.
 *
 * @since 0.0.2
 * @author nathanielbunch
 */
@JsonInclude
public class Transaction implements LedgerHashableItem {

    private final ZonedDateTime timestamp;
    private final String originAddress;
    @JsonIgnore
    private final PublicKey origin;
    private final String destinationAddress;
    private final TransactionInput[] txnInputs;
    private final TransactionOutput[] txnOutPuts;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] signature;
    @JsonSerialize(using = HashSerializer.class)
    private byte[] txnHash;

    protected Transaction(Builder builder) throws NoSuchAlgorithmException {
        this.timestamp = builder.timestamp;
        this.originAddress = builder.originAddress;
        this.origin = builder.origin;
        this.destinationAddress = builder.destinationAddress;
        this.txnInputs = builder.txnInputs;
        this.txnOutPuts = builder.txnOutPuts;
        this.txnHash = CryptoHasher.hash(this);
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public PublicKey getOrigin() {
        return origin;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public byte[] getTxnHash() {
        return txnHash;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
    }

    public TransactionInput[] getTxnInputs() {
        return txnInputs;
    }

    public TransactionOutput[] getTxnOutPuts() {
        return txnOutPuts;
    }

    @JsonIgnore
    public byte[] rehash() throws NoSuchAlgorithmException {
        this.txnHash = CryptoHasher.hash(this);
        return this.txnHash;
    }

    public boolean compareTxnHash(byte[] txnHash) {
        try {
            for (int i = 0; i < txnHash.length; i++) {
                if (this.txnHash[i] != txnHash[i]) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public boolean isCoinbase() {
        return (this.txnInputs.length == 1 && this.txnInputs[0].txnOutPt == null);
    }

    @Override
    public String toString(){
        return CryptoHasher.humanReadableHash(txnHash);
    }

    @JsonIgnore
    @Override
    public byte[] getDataBytes() {
        byte[] txnData = new byte[0];
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.timestamp));
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.originAddress));
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.origin));
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.destinationAddress));
        txnData = appendBytes(txnData, this.signature);
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.txnInputs));
        txnData = appendBytes(txnData, SerializationUtils.serialize(this.txnOutPuts));
        return txnData;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        protected ZonedDateTime timestamp;
        protected String originAddress;
        protected PublicKey origin;
        protected String destinationAddress;
        protected TransactionInput[] txnInputs;
        protected TransactionOutput[] txnOutPuts;

        private Builder(){}

        public Builder setOriginAddress(String originAddress) {
            this.originAddress = originAddress;
            return this;
        }

        public Builder setOriginPublicKey(PublicKey origin) {
            this.origin = origin;
            return this;
        }

        public Builder setDestinationAddress(String destination) {
            this.destinationAddress = destination;
            return this;
        }

        public Builder setTxnInputs(TransactionInput[] txnInputs){
            this.txnInputs = txnInputs;
            return this;
        }

        public Builder setTxnOutputs(TransactionOutput[] txnOutPuts) {
            this.txnOutPuts = txnOutPuts;
            return this;
        }

        public Builder setTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Transaction build() throws NoSuchAlgorithmException {
            return new Transaction(this);
        }

        public Transaction buildFromMessage(TransactionPayload transactionMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            this.timestamp = DateTimeUtil.fromMessageTimestamp(transactionMessage.getTimestamp());
            this.origin = CryptoKeyGenerator.readPublicKeyFromBytes(transactionMessage.getOriginAddress());
            this.destinationAddress = String.valueOf(transactionMessage.getDestinationAddress());
            Transaction txn = new Transaction(this);
            txn.signature = transactionMessage.getSignature();
            return txn;
        }

        public Transaction buildFromMessage(MempoolTransactionPayload transactionMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            this.timestamp = DateTimeUtil.fromMessageTimestamp(transactionMessage.getTimestamp());
            this.origin = CryptoKeyGenerator.readPublicKeyFromBytes(CryptoHasher.hashByteArray(String.valueOf(transactionMessage.getOriginAddress())));
            this.destinationAddress = String.valueOf(transactionMessage.getDestinationAddress());
            Transaction txn = new Transaction(this);
            txn.signature = transactionMessage.getSignature();
            return txn;
        }
    }

}
