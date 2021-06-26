package org.yggdrasil.core.ledger.transaction;

import java.math.BigDecimal;

public class TransactionOutput {

    // public key of the wallet in question
    protected final byte[] destination;
    protected final BigDecimal value;

    public TransactionOutput(byte[] destination, BigDecimal value) {
        this.destination = destination;
        this.value = value;
    }

    public boolean isMine(byte[] signature) {
        //verify that the public key and signature can be validated.
        return false;
    }

    public BigDecimal getValue() {
        return this.value;
    }

}
