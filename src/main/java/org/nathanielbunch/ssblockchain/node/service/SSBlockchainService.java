package org.nathanielbunch.ssblockchain.node.service;

import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
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
    public void addNewTransaction(SSTransaction transaction){
        logger.info("New transaction: {} [{} -> {} = {}]", transaction.toString(), transaction.getOrigin(), transaction.getDestination(), transaction.getAmount());
        this.transactions.add(transaction);
    }

}
