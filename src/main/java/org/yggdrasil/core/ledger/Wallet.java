package org.yggdrasil.core.ledger;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.DateTimeUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * The Wallet will contain the ability to send, receive, and store crypto
 * in an encrypted environment. The Wallet will also be capable of creating
 * an identifiable address in order to send and receive value.
 *
 * @since 0.0.5
 * @author nathanielbunch
 */
@JsonInclude
public class Wallet implements Serializable {

    // Add details here.
    // Should include balance, address, etc...
    private final PublicKey publicKey;
    private final UUID index;
    private final ZonedDateTime creationDate;
    private final byte[] address;
    private final BigDecimal balance;
    private final byte[] walletHash;

    private Wallet(WBuilder builder) throws NoSuchAlgorithmException {
        this.publicKey = builder.publicKey;
        this.index = builder.index;
        this.creationDate = builder.creationDate;
        this.address = builder.address;
        this.balance = BigDecimal.ZERO;
        this.walletHash = CryptoHasher.hash(this);
    }

    private Wallet(PublicKey publicKey, UUID index, ZonedDateTime creationDate, byte[] address, BigDecimal balance, byte[] walletHash) {
        this.publicKey = publicKey;
        this.index = index;
        this.creationDate = creationDate;
        this.address = address;
        this.balance = balance;
        this.walletHash = walletHash;
    }

    public UUID getIndex() {
        return index;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public byte[] getAddress() {
        return address;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    protected Wallet updateBalance(BigDecimal delta, boolean isNegative) {
        BigDecimal newBalance;
        if(isNegative){
            newBalance = this.balance.subtract(delta);
        } else {
            newBalance = this.balance.add(delta);
        }
        return new Wallet(this.publicKey, this.index, this.creationDate, this.address, newBalance, walletHash);
    }

    public byte[] getWalletHash() {
        return walletHash;
    }

    public String getHumanReadableAddress() {
        return "0x" + CryptoHasher.humanReadableHash(address);
    }

    @Override
    public String toString() {
        return CryptoHasher.humanReadableHash(walletHash);
    }

    /**
     * WBuilder class is the SSWallet builder. This is to ensure some level
     * of data protection by enforcing non-direct data access and immutable data.
     */
    public static class WBuilder {

        private PublicKey publicKey;
        private UUID index;
        private ZonedDateTime creationDate;
        private byte[] address;

        private WBuilder(){}

        public static WBuilder newSSWalletBuilder() {
            return new WBuilder();
        }

        public WBuilder setPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Wallet build() throws NoSuchAlgorithmException {
            this.index = UUID.randomUUID();
            this.creationDate = DateTimeUtil.getCurrentTimestamp();
            this.address = this.buildWalletAddress(publicKey.getEncoded());
            return new Wallet(this);
        }

        private byte[] buildWalletAddress(byte[] publicKeyEncoded) {
            byte[] address = new byte[20];
            int j = 0;
            for(int i = publicKeyEncoded.length-20; i < publicKeyEncoded.length; i++){
                address[j] = publicKeyEncoded[i];
                j++;
            }
            return address;
        }
    }
}
