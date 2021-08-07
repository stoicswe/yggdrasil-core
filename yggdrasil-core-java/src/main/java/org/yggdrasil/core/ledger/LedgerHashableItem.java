package org.yggdrasil.core.ledger;

import java.io.Serializable;

public interface LedgerHashableItem extends Serializable {

    byte[] getDataBytes();

}
