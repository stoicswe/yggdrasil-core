package org.yggdrasil.core.ledger.transaction;

import org.yggdrasil.core.utils.CryptoHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    private Logger logger = LoggerFactory.getLogger(Mempool.class);

    private List<Transaction> transactionPool;

    @PostConstruct
    private void init() {
        this.transactionPool = new ArrayList<>();
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

    public Optional<Transaction> getTransaction(byte[] txnHash){
        logger.trace("In getTransaction with transaction hash: {}", CryptoHasher.humanReadableHash(txnHash));
        return transactionPool.stream().filter(txn -> txn.compareTxnHash(txnHash)).findFirst();
    }

}