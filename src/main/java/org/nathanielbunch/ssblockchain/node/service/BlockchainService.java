package org.nathanielbunch.ssblockchain.node.service;

import org.nathanielbunch.ssblockchain.core.ledger.Block;
import org.nathanielbunch.ssblockchain.core.ledger.Blockchain;
import org.nathanielbunch.ssblockchain.core.ledger.Transaction;
import org.nathanielbunch.ssblockchain.core.ledger.Wallet;
import org.nathanielbunch.ssblockchain.core.utils.BCOHasher;
import org.nathanielbunch.ssblockchain.core.utils.BCOKeyGenerator;
import org.nathanielbunch.ssblockchain.node.controller.BlockchainController;
import org.nathanielbunch.ssblockchain.node.model.BlockResponse;
import org.nathanielbunch.ssblockchain.node.network.Node;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.DestroyFailedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Handles lower-level operation with the SSBlockchain. Used
 * to serve functionality to the rest endpoint.
 *
 * @since 0.0.1
 * @see BlockchainController
 * @author nathanielbunch
 */
@Service
public class BlockchainService {

    private final Integer _PREFIX = 4;
    private final Logger logger = LoggerFactory.getLogger(BlockchainService.class);
    private final Object lock = new Object();

    @Autowired
    private Node node;
    @Autowired
    private Blockchain blockchain;
    @Autowired
    private BCOKeyGenerator keyGenerator;

    private List<Transaction> transactions;
    private Wallet currentWallet;

    @PostConstruct
    private void init(){
        this.transactions = new ArrayList<>();
    }

    /**
     * Returns the current local blockchain instance.
     *
     * @return
     */
    public Blockchain getBlockchain() {
        return this.blockchain;
    }

    /**
     * Returns a transaction given a set of identifying parameters.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Transaction getTransaction() throws NoSuchAlgorithmException {
        // This returns a dummy transaction for now, but at some point may have a lookup service.
        // Primarily for testing serialization.
        return Transaction.TBuilder.newSSTransactionBuilder()
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
    public void addNewTransaction(Transaction transaction) {
        synchronized (lock) {
            logger.info("New transaction: {} [{} -> {} = {}]", transaction.toString(), transaction.getOrigin(), transaction.getDestination(), transaction.getAmount());
            this.transactions.add(transaction);
        }
    }

    /**
     * Returns the currently loaded wallet.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Wallet getWallet() throws NoSuchAlgorithmException, DestroyFailedException {
        logger.info("Generating new wallet...");
        KeyPair newKeyPair = keyGenerator.generatePublicPrivateKeys();
        Wallet newWallet = Wallet.WBuilder.newSSWalletBuilder().setPublicKey(newKeyPair.getPublic()).build();
        logger.info("New wallet generated with the private key: {}", BCOHasher.humanReadableHash(newKeyPair.getPrivate().getEncoded()));
        this.currentWallet = newWallet;
        return newWallet;
    }

    /**
     * Returns a most recent block response.
     *
     * @return
     */
    public BlockResponse mineBlock() throws Exception {

        logger.info("Mining new block...");

        BlockResponse lastMinedBlock;

        if(blockchain.getBlocks().length == 0){
            Block genesisBlock = Block.BBuilder.newSSBlockBuilder()
                    .setData("In the beginning...there was light.")
                    .setPreviousBlock(null)
                    .build();
            blockchain.addBlocks(List.of(genesisBlock));
        }

        Block lastBlock = blockchain.getBlocks()[blockchain.getBlocks().length-1];

        logger.info("Last block record: {}", lastBlock.toString());

        Block newBlock;
        synchronized (lock) {
            newBlock = Block.BBuilder.newSSBlockBuilder()
                    .setData(this.transactions.toArray(Transaction[]::new))
                    .setPreviousBlock(this.blockchain.getBlocks()[this.blockchain.getBlocks().length - 1].getBlockHash())
                    .build();

            this.transactions = new ArrayList<>();
        }

        // Do some work
        newBlock = this.proofOfWork(_PREFIX, newBlock);
        this.blockchain.addBlocks(List.of(newBlock));

        logger.info("New block: {}", newBlock.toString());

        Transaction blockMineAward = Transaction.TBuilder.newSSTransactionBuilder()
                .setOrigin("SSBlockchainNetwork")
                .setDestination(currentWallet.getHumanReadableAddress())
                .setValue(new BigDecimal(newBlock.toString().length() / 9.23).setScale(12, RoundingMode.FLOOR))
                .setNote("Happy mining!")
                .build();

        this.addNewTransaction(blockMineAward);

        logger.info("Block mine awarded, transaction: {} @ {}", blockMineAward.toString(), blockMineAward.getAmount());

        return BlockResponse.Builder.builder()
                .setIndex(newBlock.getIndex())
                .setTimestamp(newBlock.getTimestamp())
                .setSize(GraphLayout.parseInstance(newBlock).totalSize())
                .setBlockhash(newBlock.getBlockHash())
                .build();
    }

    private Block proofOfWork(int prefix, Block currentBlock) throws Exception {
        List<Transaction> blockTransactions = new ArrayList<>(Arrays.asList((Transaction[]) currentBlock.getData()));
        blockTransactions.sort(Comparator.comparing(Transaction::getTimestamp));
        Block sortedBlock = Block.BBuilder.newSSBlockBuilder()
                .setPreviousBlock(currentBlock.getPreviousBlockHash())
                .setData(blockTransactions)
                .build();
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!sortedBlock.toString().substring(0, prefix).equals(prefixString)) {
            sortedBlock.incrementNonce();
            sortedBlock.setBlockHash(BCOHasher.hash(sortedBlock));
        }
        return sortedBlock;
    }

}
