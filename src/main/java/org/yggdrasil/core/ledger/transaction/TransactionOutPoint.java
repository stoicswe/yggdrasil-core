package org.yggdrasil.core.ledger.transaction;

import org.yggdrasil.core.utils.CryptoHasher;

import java.math.BigDecimal;

public class TransactionOutPoint {

    // Txn hash
    protected final byte[] hash;
    // Txn output value
    protected final BigDecimal value;

    public TransactionOutPoint() {
        this.hash = new byte[0];
        this.value = BigDecimal.valueOf(-1);
    }

    public TransactionOutPoint(byte[] hash, BigDecimal value) {
        this.hash = hash;
        this.value = value;
    }

    public boolean isNull() {
        return (this.hash.length == 0 && this.value.compareTo(BigDecimal.valueOf(-1)) == 0);
    }

    public int compareTo(TransactionOutPoint txnOutPt) {
        int result = CryptoHasher.compareHashes(this.hash, txnOutPt.hash);
        if(result == 0) {
            return this.value.compareTo(txnOutPt.value);
        }
        return result;
    }

    public boolean equalTo(TransactionOutPoint txnOutPt) {
        return (CryptoHasher.isEqualHashes(this.hash, txnOutPt.hash) && this.value.compareTo(txnOutPt.value) == 0);
    }

}
