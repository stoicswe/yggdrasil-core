package org.yggdrasil.core.ledger.transaction;

import java.math.BigDecimal;

public class TransactionInput {

    // Reference to a transaction's output
    protected final TransactionOutPoint txnOutPt;
    // Value of the input
    // If this value is < txnOutPt, then there needs to
    // be changed delivered. If it is greater than, need
    // to check the other txnInputs to before failed validation

    // this value below could be for the transaction fee...
    protected final BigDecimal value;


    public TransactionInput(){
        this.txnOutPt = null;
        this.value = BigDecimal.valueOf(-1);
    }

    public TransactionInput(TransactionOutPoint txnOut, BigDecimal value){
        this.txnOutPt = txnOut;
        this.value = value;
    }

    public TransactionInput(byte[] prevTxHash, BigDecimal valueOut){
        this.txnOutPt = new TransactionOutPoint(prevTxHash, valueOut);
        this.value = valueOut;
    }

}
