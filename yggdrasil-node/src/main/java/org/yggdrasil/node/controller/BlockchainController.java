package org.yggdrasil.node.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.BasicTransaction;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.wallet.Wallet;
import org.yggdrasil.core.utils.CryptoHasher;
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
import java.util.Optional;

/**
 * Provides the rest interface controller for interacting with the Blockchain.
 *
 * @since 0.0.3
 * @author nathanielbunch
 */
@RestController
@ConditionalOnExpression("${blockchain.api.enabled:false}")
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
        return new ResponseEntity<>(this.service.getBlockchain(), HttpStatus.OK);
    }

    @RequestMapping(value = "/block", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Block>> getBlock(@RequestParam(name = "blockHash", required = true) String blockHash) throws Exception {
        return new ResponseEntity<>(this.service.getBlock(CryptoHasher.hashByteArray(blockHash)), HttpStatus.OK);
    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity mineBlock() throws Exception {
        this.service.mineBlock();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/wallet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Wallet>> getWallet(@RequestParam(name = "allWallets", required = false) boolean allWallets) throws Exception {
        return new ResponseEntity<>(this.service.getWallet(allWallets), HttpStatus.OK);
    }

    @RequestMapping(value = "/createWallet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Wallet> createNewWallet(@RequestParam(name = "walletName", required = true) String walletName) throws Exception {
        return new ResponseEntity<>(this.service.createWallet(walletName), HttpStatus.OK);
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
