package org.yggdrasil.node.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.wallet.Wallet;
import org.yggdrasil.node.model.BlockResponse;
import org.yggdrasil.node.service.BlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Provides the rest interface controller for interacting with the Blockchain.
 *
 * @since 0.0.3
 * @author nathanielbunch
 */
@RestController
public class BlockchainController {

    Logger logger = LoggerFactory.getLogger(BlockchainController.class);

    private ObjectMapper objectMapper;

    @Autowired
    BlockchainService service;

    @PostConstruct
    private void init() {
        this.objectMapper = new ObjectMapper();
    }

    @RequestMapping(value = "/blocks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Blockchain> getBlockchain() throws Exception {
        return new ResponseEntity<>(this.service.getBlockchain(), HttpStatus.OK);
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BlockResponse> mineBlock() throws Exception {
        return new ResponseEntity<>(this.service.mineBlock(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/wallet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Wallet> getWallet() throws Exception {
        return new ResponseEntity<>(this.service.getWallet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> putTransaction(@RequestBody JsonNode data) throws IOException, NoSuchAlgorithmException {
        logger.trace("Received new data: {}", data);
        Transaction transaction = objectMapper.treeToValue(data, Transaction.class);
        this.service.addNewTransaction(transaction);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getTransaction(@RequestParam(name = "transactions", required = false) Integer transactions) throws Exception {
        if(transactions == null || transactions <= 0) {
            transactions = 1;
        }
        return new ResponseEntity<>(this.service.getTransaction(transactions), HttpStatus.OK);
    }

    // Used to test in-development features
    @RequestMapping(value = "/testFeature", method = RequestMethod.HEAD)
    public void testFeature() throws Exception {
        this.service.testSigning();
    }

}
