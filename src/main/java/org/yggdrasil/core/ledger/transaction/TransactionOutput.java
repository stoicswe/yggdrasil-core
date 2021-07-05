package org.yggdrasil.core.ledger.transaction;

import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.*;

public class TransactionOutput implements Serializable {

    // public key of the wallet in question
    // receiver must be able to sign an output in order
    // claim it.
    protected final PublicKey publicKey;
    protected final BigDecimal value;

    public TransactionOutput(PublicKey publicKey, BigDecimal value) {
        this.publicKey = publicKey;
        this.value = value;
    }

    public boolean isMine(byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature verify = Signature.getInstance(CryptoKeyGenerator.getSignatureAlgorithm());
        verify.initVerify(publicKey);
        //verify.update(txn.getTxnHash());
        return verify.verify(signature);
    }

    public BigDecimal getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("TxOut(val=%d, key=%s)", value.intValue(), CryptoHasher.humanReadableHash(publicKey.getEncoded()));
    }

}
