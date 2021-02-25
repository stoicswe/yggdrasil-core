package org.nathanielbunch.ssblockchain.node.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.nathanielbunch.ssblockchain.core.ledger.SSWallet;
import org.nathanielbunch.ssblockchain.node.model.SSBlockResponse;
import org.nathanielbunch.ssblockchain.node.service.SSBlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * Provides the rest interface for interacting with the SSBlockchain.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
@RestController
public class SSRestController {

    Logger logger = LoggerFactory.getLogger(SSRestController.class);

    private ObjectMapper objectMapper;

    @Autowired
    SSBlockchainService service;

    @PostConstruct
    private void init() {
        this.objectMapper = new ObjectMapper();
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SSTransaction> putSSTransaction(@RequestBody JsonNode data) throws JsonProcessingException {
        logger.trace("Received new data: {}", data);
        SSTransaction transaction = objectMapper.treeToValue(data, SSTransaction.class);
        this.service.addNewTransaction(transaction);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SSTransaction> getSSTransaction() throws Exception {
        return new ResponseEntity<>(this.service.getTransaction(), HttpStatus.OK);
    }

    @RequestMapping(value = "/wallet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SSWallet> getSSWallet() throws Exception {
        return new ResponseEntity<>(this.service.getWallet(), HttpStatus.OK);
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SSBlockResponse> mineBlock() throws Exception {
        return new ResponseEntity<>(this.service.mineBlock(), HttpStatus.CREATED);
    }

}
