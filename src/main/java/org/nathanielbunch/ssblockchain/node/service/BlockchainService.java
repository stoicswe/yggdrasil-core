package org.nathanielbunch.ssblockchain.node.service;

import org.nathanielbunch.ssblockchain.core.ledger.chain.Block;
import org.nathanielbunch.ssblockchain.core.ledger.chain.Blockchain;
import org.nathanielbunch.ssblockchain.core.ledger.transaction.Mempool;
import org.nathanielbunch.ssblockchain.core.ledger.transaction.Txn;
import org.nathanielbunch.ssblockchain.core.ledger.Wallet;
import org.nathanielbunch.ssblockchain.core.utils.CryptoHasher;
import org.nathanielbunch.ssblockchain.core.utils.CryptoKeyGenerator;
import org.nathanielbunch.ssblockchain.node.controller.BlockchainController;
import org.nathanielbunch.ssblockchain.node.model.BlockResponse;
import org.nathanielbunch.ssblockchain.node.network.Node;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final Integer _MAX_BLOCK_SIZE = 52;

    private final Logger logger = LoggerFactory.getLogger(BlockchainService.class);
    private final Object lock = new Object();

    @Autowired
    private Node node;
    @Autowired
    private Blockchain blockchain;
    @Autowired
    private Mempool mempool;
    @Autowired
    private CryptoKeyGenerator keyGenerator;

    private Wallet currentWallet;

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
    public Txn getTransaction() throws NoSuchAlgorithmException {
        // This returns a dummy transaction for now, but at some point may have a lookup service.
        // Primarily for testing serialization.
        return Txn.Builder.newSSTransactionBuilder()
                .setOrigin("TestAddress")
                .setDestination("TestDestination")
                .setValue(new BigDecimal("0.1234"))
                .setNote("Test transaction")
                .build();
    }

    /**
     * Adds a new transaction to execute on the blockchain.
     *
     * @param txn
     */
    public void addNewTransaction(Txn txn) {
        logger.info("New transaction: {} [{} -> {} = {}]", txn.toString(), txn.getOrigin(), txn.getDestination(), txn.getAmount());
        this.mempool.putTransaction(txn);
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
        logger.info("New wallet generated with the private key: {}", CryptoHasher.humanReadableHash(newKeyPair.getPrivate().getEncoded()));
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
        Block lastBlock = blockchain.getBlocks()[blockchain.getBlocks().length-1];
        logger.info("Last block record: {}", lastBlock.toString());

        List<Txn> blockData = new ArrayList<>();
        while(mempool.hasNext()) {
            if(!(blockData.size() < _MAX_BLOCK_SIZE)) {
                blockData.add(mempool.getTransaction());
            } else {
                break;
            }
        }
        Block newBlock;
        newBlock = Block.BBuilder.newSSBlockBuilder()
                .setData(blockData.toArray(Txn[]::new))
                .setPreviousBlock(this.blockchain.getBlocks()[this.blockchain.getBlocks().length - 1].getBlockHash())
                .build();

        // Do some work
        newBlock = this.proofOfWork(_PREFIX, newBlock);
        this.blockchain.addBlocks(List.of(newBlock));

        logger.info("New block: {}", newBlock.toString());

        Txn blockMineAward = Txn.Builder.newSSTransactionBuilder()
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

    // This will be replaced with the validator, using PoS as the system for validation
    private Block proofOfWork(int prefix, Block currentBlock) throws Exception {
        List<Txn> blockTxns = new ArrayList<>(Arrays.asList((Txn[]) currentBlock.getData()));
        blockTxns.sort(Comparator.comparing(Txn::getTimestamp));
        Block sortedBlock = Block.BBuilder.newSSBlockBuilder()
                .setPreviousBlock(currentBlock.getPreviousBlockHash())
                .setData(blockTxns)
                .build();
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!sortedBlock.toString().substring(0, prefix).equals(prefixString)) {
            sortedBlock.incrementNonce();
            sortedBlock.setBlockHash(CryptoHasher.hash(sortedBlock));
        }
        return sortedBlock;
    }

}
