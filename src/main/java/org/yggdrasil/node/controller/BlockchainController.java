package org.yggdrasil.node.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.BasicTransaction;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
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
    public ResponseEntity<Blockchain> getBlockchain(@RequestParam(name = "blocks", required = false) Integer blocks) throws Exception {
        return new ResponseEntity<>(this.service.getBlockchain((blocks != null) ? blocks : -1), HttpStatus.OK);
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BlockResponse> mineBlock() throws Exception {
        return new ResponseEntity<>(this.service.mineBlock(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/wallet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Wallet> getCurrentWallet() throws Exception {
        return new ResponseEntity<>(this.service.getWallet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/createWallet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Wallet> createNewWallet() throws Exception {
        return new ResponseEntity<>(this.service.createWallet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/selectWallet", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Wallet> switchNewWallet(@RequestParam(name = "address") String address) throws Exception {
        return new ResponseEntity<>(this.service.selectWallet(address), HttpStatus.OK);
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasicTransaction> putTransaction(@RequestBody JsonNode data) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        logger.trace("Received new data: {}", data);
        BasicTransaction transaction = objectMapper.treeToValue(data, BasicTransaction.class);
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
