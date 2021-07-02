package org.yggdrasil.core.ledger.wallet;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.node.network.NodeConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.TimeUnit;

@Component
public class WalletIndexer {

    private final Logger logger = LoggerFactory.getLogger(WalletIndexer.class);

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private CryptoKeyGenerator cryptoKeyGenerator;
    private DB coldWallets;
    private HTreeMap walletData;
    private DB hotWallets;
    private HTreeMap walletCache;

    @PostConstruct
    private void init() {
        this.coldWallets = DBMaker
                .fileDB(nodeConfig._CURRENT_DIRECTORY + "/wallet" + nodeConfig._FILE_EXTENSION)
                .transactionEnable()
                .make();
        this.walletData = coldWallets.hashMap("walletData")
                .keySerializer(Serializer.BYTE_ARRAY)
                .counterEnable()
                .createOrOpen();
        this.hotWallets = DBMaker
                .memoryDirectDB()
                .transactionEnable()
                .make();
        this.walletCache = hotWallets
                .hashMap("walletCache")
                .keySerializer(Serializer.BYTE_ARRAY)
                .expireOverflow(walletData)
                .expireAfterGet(5, TimeUnit.SECONDS)
                .create();
    }

    @PreDestroy
    private void onDestroy() {
        logger.info("Shutting down wallet database.");
        this.walletCache.clearWithExpire();
        this.walletData.close();
    }

    /**
     * Create a new wallet and add to the walletCache.
     * @return
     */
    public Wallet createNewWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet w = Wallet.Builder.newBuilder()
                .setKeyPair(cryptoKeyGenerator.generatePublicPrivateKeys())
                .build();
        this.walletCache.put(w.getAddress(), w);
        this.hotWallets.commit();
        return w;
    }

    public Wallet getWallet(byte[] address) {
        return (Wallet) this.walletCache.get(address);
    }

    public void deleteWallet(byte[] address) {
        this.walletCache.remove(address);
        this.walletData.remove(address);
    }

}
