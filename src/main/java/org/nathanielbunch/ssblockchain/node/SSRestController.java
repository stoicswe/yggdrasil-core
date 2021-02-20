package org.nathanielbunch.ssblockchain.node;

import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SSRestController {

    Logger logger = LoggerFactory.getLogger(SSRestController.class);

    private List<SSTransaction> transactions;

    @PostConstruct
    private void init() {
        transactions = new ArrayList<>();
    }

    @PutMapping("/transaction")
    public void putSSTransaction(SSTransaction transaction) {
        logger.debug("New transaction: {} [{} -> {} = {}]", transaction.toString(), transaction.getOrigin(), transaction.getDestination(), transaction.getAmount());
        this.transactions.add(transaction);
    }

    @GetMapping("/transaction")
    public SSTransaction getSStransaction() throws Exception {
        return SSTransaction.TBuilder.newSSTransactionBuilder()
                .setOrigin("TestAddr")
                .setDestination("TestAddr2")
                .setAmountValue(5.0)
                .setNote("Test")
                .build();
    }

}
