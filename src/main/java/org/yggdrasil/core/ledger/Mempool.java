package org.yggdrasil.core.ledger;

import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This component in the blockchain is used for temporary storage of transactions.
 * Transactions stored here are ones that have been verified (received) but have yet
 * to be placed and processed into a block.
 *
 * @since 0.0.9
 * @author nathanielbunch
 */
@Component
public class Mempool {

    private final Logger logger = LoggerFactory.getLogger(Mempool.class);

    private List<Transaction> transactionPool;

    @PostConstruct
    private void init() {
        this.transactionPool = new ArrayList<>();
    }

    public int size() {
        return transactionPool.size();
    }

    public void putTransaction(Transaction transaction) {
        logger.trace("In putTransaction");
        this.transactionPool.add(transaction);
        logger.debug("New transaction added to the mempool: {}", transaction.toString());
    }

    public boolean hasNext() {
        return this.transactionPool.size() > 0;
    }

    public Transaction getTransaction() {
        logger.trace("In getTransaction");
        if(transactionPool.size() > 0) {
            Transaction transaction = transactionPool.get(0);
            transactionPool.remove(0);
            logger.debug("Retrieved next transaction from the mempool: {}", transaction.toString());
            return transaction;
        } else {
            logger.debug("Tried to get a transaction from an empty mempool.");
            return null;
        }
    }

    public List<Transaction> getTransaction(int count) {
        logger.trace("In getTransaction");
        if(transactionPool.size() < count) {
            count = transactionPool.size();
        }
        List<Transaction> txns = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            txns.add(transactionPool.get(0));
            transactionPool.remove(0);
        }
        return txns;
    }

    public List<Transaction> peekTransaction(int numberToPeek) throws NoSuchAlgorithmException {
        logger.trace("In peekTransaction");
        List<Transaction> peekedTxns = new ArrayList<>();
        if(transactionPool.size() > 0) {
            if(numberToPeek >= transactionPool.size()) {
                numberToPeek = 0;
            } else {
                numberToPeek = transactionPool.size() - numberToPeek;
            }
            for(int i = transactionPool.size()-1; i >= numberToPeek; i--){
                peekedTxns.add(transactionPool.get(i));
            }
            logger.debug("Retrieved {} transactions from the mempool", numberToPeek);
            return peekedTxns;
        } else {
            logger.debug("Tried to get a transaction from an empty mempool.");
            return new ArrayList<>();
        }
    }

    public Optional<Transaction> getTransaction(byte[] txnHash){
        logger.trace("In getTransaction with transaction hash: {}", CryptoHasher.humanReadableHash(txnHash));
        return transactionPool.stream().filter(txn -> txn.compareTxnHash(txnHash)).findFirst();
    }

}
