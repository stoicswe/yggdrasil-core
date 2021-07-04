package org.yggdrasil.node.service;

import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.transaction.BasicTransaction;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.wallet.Wallet;
import org.yggdrasil.core.ledger.wallet.WalletIndexer;
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
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
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
    private final Integer _MAX_BLOCK_SIZE = 2048;
    private final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

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
    @Autowired
    private WalletIndexer walletIndexer;

    private Wallet currentWallet;

    /**
     * Returns the current local blockchain instance.
     *
     * @return
     */
    public Blockchain getBlockchain(int blocks) {
        // add some code so that the last # of blocks are retrieved
        // and return to the caller.
        return this.blockchain;
    }

    /**
     * Returns a transaction given a set of identifying parameters.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public List<BasicTransaction> getTransaction(int numberOfTransactions) throws NoSuchAlgorithmException {
        return this.mempool.peekTransaction(numberOfTransactions);
    }

    /**
     * Adds a new transaction to execute on the blockchain.
     *
     * @param transaction
     */
    public void addNewTransaction(BasicTransaction transaction) throws IOException, NoSuchAlgorithmException {
        logger.info("New transaction: {} [{} -> {}]", transaction.toString(), transaction.getOriginAddress(), transaction.getDestinationAddress());
        this.mempool.putTransaction(transaction);
        // need to broadcast the basic transaction.
        /*
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
        */
    }

    /**
     * Returns the currently loaded wallet.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Wallet getWallet() {
        return currentWallet;
    }

    /**
     * Create a new wallet. Returns the newly created wallet.
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     */
    public Wallet createWallet() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        logger.info("Generating new wallet...");
        Wallet wallet = this.walletIndexer.createNewWallet();
        logger.info("New wallet generated with address: {}", CryptoHasher.humanReadableHash(wallet.getAddress()));
        currentWallet = wallet;
        return wallet;
    }

    /**
     * Select a wallet to initiate transactions from.
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     */
    public Wallet selectWallet(String walletAddress) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        logger.debug("Selecting a wallet...");
        currentWallet = this.walletIndexer.getWallet(CryptoHasher.hashByteArray(walletAddress));
        logger.info("Wallet selected with address: {}", CryptoHasher.humanReadableHash(currentWallet.getAddress()));
        return currentWallet;
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

    public void testSigning() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InvalidKeySpecException, NoSuchProviderException {
        Transaction txn = Transaction.Builder.Builder()
                .setOrigin(currentWallet.getAddress())
                .setDestination("6ad28d3fda4e10bdc0aaf7112f7818e181defa7e")
                .build();
        this.currentWallet.signTransaction(txn);
        logger.info("signing...");
        logger.info(String.valueOf(txn));
        Signature ecdsaVerify = Signature.getInstance(CryptoKeyGenerator.getSignatureAlgorithm());
        ecdsaVerify.initVerify(currentWallet.getPublicKey());
        ecdsaVerify.update(txn.getTxnHash());
        logger.info(String.valueOf(ecdsaVerify.verify(txn.getSignature())));
        logger.info("done");
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
                //blockData.add(mempool.getTransaction());
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
                //.setValue(new BigDecimal(newBlock.toString().length() / 9.23).setScale(12, RoundingMode.FLOOR))
                .build();

        //this.addNewTransaction(blockMineAward);

        logger.info("Block mine awarded, transaction: {}", blockMineAward.toString());

        return BlockResponse.Builder.builder()
                .setBlockHeight(newBlock.getBlockHeight())
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
