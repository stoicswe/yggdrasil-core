package org.nathanielbunch.ssblockchain.node;

import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SSBlockChainService {

    private Logger logger = LoggerFactory.getLogger(SSBlockChainService.class);

    private List<SSTransaction> transactions;

    @PostConstruct
    private void init(){
        this.transactions = new ArrayList<>();
    }

    public SSTransaction getTransaction() throws NoSuchAlgorithmException {
        return SSTransaction.TBuilder.newSSTransactionBuilder()
                .setOrigin("TestAddress")
                .setDestination("TestDestination")
                .setValue(new BigDecimal("0.1234"))
                .setNote("Test transaction")
                .build();
    }

    public void addNewTransaction(SSTransaction transaction){
        logger.info("New transaction: {} [{} -> {} = {}]", transaction.toString(), transaction.getOrigin(), transaction.getDestination(), transaction.getAmount());
        this.transactions.add(transaction);
    }

}
