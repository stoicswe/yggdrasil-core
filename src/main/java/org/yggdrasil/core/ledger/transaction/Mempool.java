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
 * @since 0.0.1
 * @author nathanielbunch
 */
@Component
public class Mempool {

    private Logger logger = LoggerFactory.getLogger(Mempool.class);

    private List<Txn> txnPool;

    @PostConstruct
    private void init() {
        this.txnPool = new ArrayList<>();
    }

    public void putTransaction(Txn txn) {
        logger.trace("In putTransaction");
        this.txnPool.add(txn);
        logger.debug("New transaction added to the mempool: {}", txn.toString());
    }

    public boolean hasNext() {
        return this.txnPool.size() > 0;
    }

    public Txn getTransaction() {
        logger.trace("In getTransaction");
        if(txnPool.size() > 0) {
            Txn txn = txnPool.get(0);
            txnPool.remove(0);
            logger.debug("Retrieved next transaction from the mempool: {}", txn.toString());
            return txn;
        } else {
            logger.debug("Tried to get a transaction from an empty mempool.");
            return null;
        }
    }

    public Optional<Txn> getTransaction(byte[] txnHash){
        logger.trace("In getTransaction with transaction hash: {}", CryptoHasher.humanReadableHash(txnHash));
        return txnPool.stream().filter(txn -> txn.compareTxnHash(txnHash)).findFirst();
    }

}
