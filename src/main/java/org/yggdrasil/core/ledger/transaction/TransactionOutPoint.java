package org.yggdrasil.core.ledger.transaction;

import org.yggdrasil.core.utils.CryptoHasher;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransactionOutPoint implements Serializable {

    // Txn hash
    // signature needs to be able to match an
    // output contained in the txn referenced here
    protected final byte[] blkHash;
    protected final byte[] txnHash;
    // Txn output value
    protected final BigDecimal value;

    public TransactionOutPoint() {
        this.blkHash = new byte[0];
        this.txnHash = new byte[0];
        this.value = BigDecimal.valueOf(-1);
    }

    public TransactionOutPoint(byte[] blkHash, byte[] txnHash, BigDecimal value) {
        this.blkHash = blkHash;
        this.txnHash = txnHash;
        this.value = value;
    }

    public boolean isNull() {
        return (this.blkHash.length == 0  && this.txnHash.length == 0 &&
                this.value.compareTo(BigDecimal.valueOf(-1)) == 0);
    }

    public int compareTo(TransactionOutPoint txnOutPt) {
        int result = CryptoHasher.compareHashes(this.blkHash, txnOutPt.blkHash);
        result = result + CryptoHasher.compareHashes(this.txnHash, txnOutPt.txnHash);
        if(result == 0) {
            return this.value.compareTo(txnOutPt.value);
        }
        return result;
    }

    public boolean equalTo(TransactionOutPoint txnOutPt) {
        return (CryptoHasher.isEqualHashes(this.blkHash, txnOutPt.blkHash) && CryptoHasher.isEqualHashes(this.txnHash, txnOutPt.txnHash) && this.value.compareTo(txnOutPt.value) == 0);
    }

    public byte[] getBlkHash() {
        return blkHash;
    }

    public byte[] getTxnHash() {
        return txnHash;
    }

    public BigDecimal getValue() {
        return value;
    }
}
