package org.yggdrasil.core.ledger.transaction;

import java.io.Serializable;
import java.math.BigInteger;


public class TransactionInPoint implements Serializable {

    protected final Transaction txn;
    protected final BigInteger value;

    public TransactionInPoint() {
        this.txn = null;
        this.value = BigInteger.valueOf(-1);
    }

    public TransactionInPoint(Transaction txn, BigInteger value) {
        this.txn = txn;
        this.value = value;
    }

    public boolean isNull() {
        return (this.txn == null && this.value.compareTo(BigInteger.valueOf(-1)) == 0);
    }

}
