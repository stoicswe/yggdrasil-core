package org.yggdrasil.core.ledger.transaction;

import java.math.BigDecimal;
import java.util.List;

public class WalletTransaction {

    private List<byte[]> cMerkle;

    public BigDecimal getCredits() {
        return BigDecimal.ONE;
    }

}
