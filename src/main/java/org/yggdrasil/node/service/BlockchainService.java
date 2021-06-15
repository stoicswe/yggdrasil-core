package org.yggdrasil.node.service;

import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.transaction.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.Wallet;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.node.controller.BlockchainController;
import org.yggdrasil.node.model.BlockResponse;
import org.yggdrasil.node.network.Node;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.NetworkType;
import org.yggdrasil.node.network.messages.enums.RequestType;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;
import org.yggdrasil.node.network.messages.payloads.TransactionMessage;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;

import javax.security.auth.DestroyFailedException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Handles lower-level operation with the Blockchain. Used
 * to serve functionality to the rest endpoint.
 *
 * @since 0.0.3
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
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;
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
    public List<Transaction> getTransaction(int numberOfTransactions) throws NoSuchAlgorithmException {
        // This returns a dummy transaction for now, but at some point may have a lookup service.
        // Primarily for testing serialization.
        return this.mempool.peekTransaction(numberOfTransactions);
    }

    /**
     * Adds a new transaction to execute on the blockchain.
     *
     * @param transaction
     */
    public void addNewTransaction(Transaction transaction) throws IOException, NoSuchAlgorithmException {
        logger.info("New transaction: {} [{} -> {} = {}]", transaction.toString(), CryptoHasher.humanReadableHash(transaction.getOrigin()), CryptoHasher.humanReadableHash(transaction.getDestination()), transaction.getValue());
        this.mempool.putTransaction(transaction);
        TransactionPayload txnPayload = TransactionPayload.Builder.newBuilder()
                .buildFromTransaction(transaction)
                .setBlockHash(new byte[0])
                .build();
        TransactionMessage txnMessage = TransactionMessage.Builder.newBuilder()
                .setTxnCount(1)
                .setTxns(new TransactionPayload[]{txnPayload})
                .build();
        Message txnMsg = Message.Builder.newBuilder()
                .setNetwork(nodeConfig.getNetwork())
                .setRequestType(RequestType.DATA_RESP)
                .setMessagePayload(txnMessage)
                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(txnMessage).totalSize()))
                .setChecksum(CryptoHasher.hash(txnMessage))
                .build();
        this.messenger.sendBroadcastMessage(txnMsg);
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

    public void sendMessage() throws NoSuchAlgorithmException, IOException {
        PingPongMessage pingPongMessage = PingPongMessage.Builder.newBuilder().setNonce(25).build();
        Message message = Message.Builder.newBuilder()
                .setNetwork(NetworkType.MAIN_NET)
                .setRequestType(RequestType.PING)
                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(pingPongMessage).totalSize()))
                .setMessagePayload(pingPongMessage)
                .setChecksum(CryptoHasher.hash(pingPongMessage)).build();
        messenger.sendBroadcastMessage(message);
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

        List<Transaction> blockData = new ArrayList<>();
        while(mempool.hasNext()) {
            if(blockData.size() < _MAX_BLOCK_SIZE) {
                blockData.add(mempool.getTransaction());
            } else {
                break;
            }
        }
        Block newBlock;
        newBlock = Block.Builder.newBuilder()
                .setData(blockData)
                .setPreviousBlock(this.blockchain.getBlocks()[this.blockchain.getBlocks().length - 1].getBlockHash())
                .build();

        // Do some work
        newBlock = this.proofOfWork(_PREFIX, newBlock);
        this.blockchain.addBlocks(List.of(newBlock));

        logger.info("New block: {}", newBlock.toString());

        Transaction blockMineAward = Transaction.Builder.Builder()
                .setOrigin("7c5ec4b1ad5bdfc593587f3a9d50327ede02076b")
                .setDestination(currentWallet.getHumanReadableAddress())
                .setValue(new BigDecimal(newBlock.toString().length() / 9.23).setScale(12, RoundingMode.FLOOR))
                .setNote("Happy mining!")
                .build();

        this.addNewTransaction(blockMineAward);

        logger.info("Block mine awarded, transaction: {} @ {}", blockMineAward.toString(), blockMineAward.getValue());

        return BlockResponse.Builder.builder()
                .setIndex(newBlock.getIndex())
                .setTimestamp(newBlock.getTimestamp())
                .setSize(GraphLayout.parseInstance(newBlock).totalSize())
                .setBlockhash(newBlock.getBlockHash())
                .build();
    }

    // This will be replaced with the validator, using PoS as the system for validation
    // This will could eventually be used for customizing the hash.
    private Block proofOfWork(int prefix, Block currentBlock) throws Exception {
        List<Transaction> blockTransactions = currentBlock.getData();
        blockTransactions.sort(Comparator.comparing(Transaction::getTimestamp));
        Block sortedBlock = Block.Builder.newBuilder()
                .setPreviousBlock(currentBlock.getPreviousBlockHash())
                .setData(blockTransactions)
                .build();
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        while (!sortedBlock.toString().substring(0, prefix).equals(prefixString)) {
            sortedBlock.incrementNonce();
            sortedBlock.setBlockHash(CryptoHasher.hash(sortedBlock));
        }
        return sortedBlock;
    }

}
