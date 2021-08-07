package org.yggdrasil.core.ledger.wallet;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class WalletRecord implements Serializable {

    private byte[] publicKey;
    private byte[] privateKey;
    private ZonedDateTime creationDate;

    public WalletRecord(Wallet wallet) {
        this.publicKey = wallet.publicKey.getEncoded();
        this.privateKey = wallet.privateKey.getEncoded();
        this.creationDate = wallet.creationDate;
    }

    protected byte[] getPublicKey() {
        return this.publicKey;
    }

    protected byte[] getPrivateKey() {
        return this.privateKey;
    }

    protected ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

}
