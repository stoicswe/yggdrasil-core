package org.yggdrasil.core.ledger.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude
public class TransactionInput implements Serializable {

    // Reference to a transaction's output
    protected final TransactionOutPoint txnOutPt;
    protected final BigDecimal value;


    public TransactionInput(){
        this.txnOutPt = null;
        this.value = BigDecimal.valueOf(-1);
    }

    public TransactionInput(TransactionOutPoint txnOut, BigDecimal value){
        this.txnOutPt = txnOut;
        this.value = value;
    }

    public TransactionInput(byte[] prevBlkHash, byte[] prevTxHash,  BigDecimal valueOut){
        this.txnOutPt = new TransactionOutPoint(prevBlkHash, prevTxHash, valueOut);
        this.value = valueOut;
    }

    public TransactionOutPoint getTxnOutPt() {
        return txnOutPt;
    }

    public BigDecimal getValue() {
        return value;
    }
}
