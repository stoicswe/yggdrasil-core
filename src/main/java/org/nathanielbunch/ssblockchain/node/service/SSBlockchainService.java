package org.nathanielbunch.ssblockchain.node.service;

import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.nathanielbunch.ssblockchain.core.ledger.SSWallet;
import org.nathanielbunch.ssblockchain.core.utils.SSHasher;
import org.nathanielbunch.ssblockchain.core.utils.SSKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.DestroyFailedException;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles lower-level operation with the SSBlockchain. Used
 * to serve functionality to the rest endpoint.
 *
 * @since 0.0.1
 * @see org.nathanielbunch.ssblockchain.node.controller.SSRestController
 * @author nathanielbunch
 */
@Service
public class SSBlockchainService {

    private Logger logger = LoggerFactory.getLogger(SSBlockchainService.class);

    @Autowired
    private SSKeyGenerator keyGenerator;

    private List<SSTransaction> transactions;

    @PostConstruct
    private void init(){
        this.transactions = new ArrayList<>();
    }

    /**
     * Returns a transaction given a set of identifying parameters.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public SSTransaction getTransaction() throws NoSuchAlgorithmException {
        return SSTransaction.TBuilder.newSSTransactionBuilder()
                .setOrigin("TestAddress")
                .setDestination("TestDestination")
                .setValue(new BigDecimal("0.1234"))
                .setNote("Test transaction")
                .build();
    }

    /**
     * Adds a new transaction to execute on the blockchain.
     *
     * @param transaction
     */
    public void addNewTransaction(SSTransaction transaction) {
        logger.info("New transaction: {} [{} -> {} = {}]", transaction.toString(), transaction.getOrigin(), transaction.getDestination(), transaction.getAmount());
        this.transactions.add(transaction);
    }

    /**
     * Returns the currently loaded wallet.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public SSWallet getWallet() throws NoSuchAlgorithmException, DestroyFailedException {
        logger.info("Generating new wallet...");
        KeyPair newKeyPair = keyGenerator.generatePublicPrivateKeys();
        SSWallet newWallet = SSWallet.WBuilder.newSSWalletBuilder().setPublicKey(newKeyPair.getPublic()).build();
        logger.info("New wallet generated with the private key: {}", SSHasher.humanReadableHash(newKeyPair.getPrivate().getEncoded()));
        return newWallet;
    }

}
