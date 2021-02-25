package org.nathanielbunch.ssblockchain.node.service;

import org.nathanielbunch.ssblockchain.core.ledger.SSBlock;
import org.nathanielbunch.ssblockchain.core.ledger.SSTransaction;
import org.nathanielbunch.ssblockchain.core.ledger.SSWallet;
import org.nathanielbunch.ssblockchain.core.utils.SSHasher;
import org.nathanielbunch.ssblockchain.core.utils.SSKeyGenerator;
import org.nathanielbunch.ssblockchain.node.model.SSBlockResponse;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.DestroyFailedException;
import java.math.BigDecimal;
import java.security.KeyPair;
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

    @Autowired
    private SSKeyGenerator keyGenerator;
    private List<SSBlock> blockchain;
    private List<SSTransaction> transactions;
    private SSWallet currentWallet;

    @PostConstruct
    private void init(){
        this.blockchain = new ArrayList<>();
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
    public void addNewTransaction(SSTransaction transaction) {
        logger.info("New transaction: {} [{} -> {} = {}]", transaction.toString(), transaction.getOrigin(), transaction.getDestination(), transaction.getAmount());
        this.transactions.add(transaction);
    }

    /**
     * Returns the currently loaded wallet.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public SSWallet getWallet() throws NoSuchAlgorithmException, DestroyFailedException {
        logger.info("Generating new wallet...");
        KeyPair newKeyPair = keyGenerator.generatePublicPrivateKeys();
        SSWallet newWallet = SSWallet.WBuilder.newSSWalletBuilder().setPublicKey(newKeyPair.getPublic()).build();
        logger.info("New wallet generated with the private key: {}", SSHasher.humanReadableHash(newKeyPair.getPrivate().getEncoded()));
        this.currentWallet = newWallet;
        return newWallet;
    }

    /**
     * Returns a most recent block response.
     *
     * @return
     */
    public SSBlockResponse mineBlock() throws Exception {

        logger.info("Mining new block...");

        SSBlockResponse lastMinedBlock;

        if(blockchain.size() == 0){
            SSBlock genesisBlock = SSBlock.BBuilder.newSSBlockBuilder()
                    .setTransactions("In the beginning...there was light.")
                    .setPreviousBlock(null)
                    .build();
            blockchain.add(genesisBlock);
        }

        SSBlock lastBlock = blockchain.get(blockchain.size()-1);

        logger.info("Last block record: {}", lastBlock.toString());

        int lastProof;
        if(lastBlock.getTransactions() instanceof String){
            lastProof = 1;
        } else {
            lastProof = ((SSTransaction[]) lastBlock.getTransactions()).length;
        }

        this.proofOfWork(lastProof);

        SSTransaction blockMineAward = SSTransaction.TBuilder.newSSTransactionBuilder()
                .setOrigin("SSBlockchainNetwork")
                .setDestination(currentWallet.getHumanReadableAddress())
                .setValue(new BigDecimal(1))
                .setNote("Happy mining!")
                .build();

        logger.info("Block mine awarded, transaction: {} @ {}", blockMineAward.toString(), blockMineAward.getAmount());

        this.addNewTransaction(blockMineAward);

        SSBlock newBlock = SSBlock.BBuilder.newSSBlockBuilder()
                .setTransactions(this.transactions.toArray(SSTransaction[]::new))
                .setPreviousBlock(this.blockchain.get(this.blockchain.size()-1).getBlockHash())
                .build();

        this.blockchain.add(newBlock);
        this.transactions = new ArrayList<>();

        logger.info("New block: {}", newBlock.toString());

        return SSBlockResponse.Builder.builder()
                .setIndex(newBlock.getIndex())
                .setTimestamp(newBlock.getTimestamp())
                .setSize(GraphLayout.parseInstance(newBlock).totalSize())
                .setBlockhash(newBlock.getBlockHash())
                .build();
    }

    private int proofOfWork(int work) {
        int incrememt = work + 1;
        while(work % 23 == 0 && incrememt % work == 0) {
            incrememt++;
        }
        return incrememt;
    }

}
