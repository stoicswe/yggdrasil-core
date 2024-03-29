package org.yggdrasil.core.ledger.wallet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
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
    private HTreeMap walletNames;
    private DB hotWallets;
    private HTreeMap walletCache;
    private HTreeMap walletNameCache;
    private Wallet currentWallet;

    @PostConstruct
    private void init() {
        this.coldWallets = DBMaker
                .fileDB(nodeConfig._CURRENT_DIRECTORY + "/wallet" + nodeConfig._FILE_EXTENSION)
                .make();
        this.walletData = coldWallets.hashMap("walletData")
                .keySerializer(Serializer.BYTE_ARRAY)
                .counterEnable()
                .createOrOpen();
        this.walletNames = coldWallets.hashMap("walletNames")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.BYTE_ARRAY)
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
                .expireAfterCreate(5, TimeUnit.SECONDS)
                .expireExecutor(Executors.newScheduledThreadPool(2))
                .createOrOpen();
        this.walletNameCache = hotWallets
                .hashMap("walletNameCache")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.BYTE_ARRAY)
                .expireOverflow(walletNames)
                .expireAfterGet(5, TimeUnit.SECONDS)
                .expireAfterCreate(5, TimeUnit.SECONDS)
                .expireExecutor(Executors.newScheduledThreadPool(2))
                .createOrOpen();
    }

    @PreDestroy
    private void onDestroy() {
        logger.info("Shutting down wallet database.");
        this.walletCache.clearWithExpire();
        this.walletNameCache.clearWithExpire();
        this.walletCache.close();
        this.walletNameCache.close();
        this.walletData.close();
        this.walletNames.close();
    }

    public Wallet getCurrentWallet() {
        return currentWallet;
    }

    public void switchCurrentWallet(Wallet wallet) {
        this.currentWallet = wallet;
    }

    /**
     * Create a new wallet and add to the walletCache.
     * @return
     */
    public Wallet createNewWallet(String walletLabel) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet w = Wallet.Builder.newBuilder()
                .setKeyPair(cryptoKeyGenerator.generatePublicPrivateKeys())
                .build();
        try {
            this.walletNameCache.put(walletLabel, w.getAddress());
            this.walletCache.put(w.getAddress(), w.toWalletRecord());
        } catch (Exception e) {
            logger.error("Error while creating new wallet: {}", e.getMessage());
            this.hotWallets.rollback();
            throw e;
        }
        this.hotWallets.commit();
        return w;
    }

    public Wallet getWallet(byte[] address) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return Wallet.Builder.newBuilder().buildFromWalletRecord((WalletRecord) this.walletCache.get(address));
    }

    public List<Pair<String, byte[]>> getAllWalletNames() {
        this.walletNameCache.clearWithExpire();
        List<Pair<String, byte[]>> results = new ArrayList<>();
        for(Object wn : this.walletNames.getKeys()){
            byte[] wa = (byte[]) walletNames.get(wn);
            results.add(new ImmutablePair<>((String) wn, wa));
        }
        return results;
    }

    public List<Wallet> getAllWallets() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        List<Wallet> wallets = new ArrayList<>();
        for(Object wa : this.walletData.getKeys()) {
            wallets.add(Wallet.Builder.newBuilder().buildFromWalletRecord((WalletRecord) this.walletCache.get(wa)));
        }
        return wallets;
    }

    public void deleteWallet(byte[] address) {
        this.walletCache.remove(address);
        this.walletData.remove(address);
    }

    public int getNumberOfWallets() {
        return this.walletData.size();
    }

}
