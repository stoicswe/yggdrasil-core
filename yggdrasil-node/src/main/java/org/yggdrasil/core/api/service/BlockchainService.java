package org.yggdrasil.core.api.service;

import org.apache.commons.lang3.tuple.Pair;
import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.BlockMine;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.exceptions.TransactionException;
import org.yggdrasil.core.ledger.transaction.*;
import org.yggdrasil.core.ledger.wallet.Wallet;
import org.yggdrasil.core.ledger.wallet.WalletIndexer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.core.api.controller.BlockchainController;
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
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionMessage;
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionPayload;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;
import org.yggdrasil.ui.MainFrame;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;

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
    private final String _APPLICATION_NAME = "Yggdrasil Core";
    private final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    // Node dependencies
    @Autowired
    private Node node;
    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;
    // Blockchain dependencies
    @Autowired
    private Blockchain blockchain;
    @Autowired
    private BlockMine blockMiner;
    @Autowired
    private Mempool mempool;
    @Autowired
    private WalletIndexer walletIndexer;

    @PostConstruct
    public void postInit() {
        // Deploy the frame
        BlockchainService blockchainService = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame(_APPLICATION_NAME, blockchainService);
            }
        });
    }

    /**
     * Returns the current local blockchain instance.
     *
     * @return
     */
    public Blockchain getBlockchain() {
        // add some code so that the last # of blocks are retrieved
        // and return to the caller.
        return this.blockchain;
    }

    public Optional<Block> getBlock(byte[] blockHash) {
        return this.blockchain.getBlock(blockHash);
    }

    /**
     * Returns a transaction given a set of identifying parameters.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public List<Transaction> getTransaction(int numberOfTransactions) throws NoSuchAlgorithmException {
        return this.mempool.peekTransaction(numberOfTransactions);
    }

    /**
     * Adds a new transaction to execute on the blockchain.
     *
     * @param transaction
     */
    public void addNewTransaction(BasicTransaction transaction) throws TransactionException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
        logger.info("Received new transaction request: {} [{} -> {}]", transaction.toString(), transaction.getOriginAddress(), transaction.getDestinationAddress());
        // build a Mempool txn
        Transaction mempoolTxn = null;
        if(CryptoHasher.isEqualHashes(this.walletIndexer.getCurrentWallet().getAddress(), CryptoHasher.hashByteArray(transaction.getOriginAddress()))) {
            mempoolTxn = Transaction.Builder.builder()
                    .setTimestamp(transaction.getTimestamp())
                    .setOriginAddress(transaction.getOriginAddress())
                    .setOriginPublicKey(this.walletIndexer.getCurrentWallet().getPublicKey())
                    .setDestinationAddress(transaction.getDestinationAddress())
                    // add the inputs and outputs to here
                    // add 50% for some realism.

                    // Values below atm are for testing.
                    .setTxnInputs(new TransactionInput[]{
                            new TransactionInput((TransactionOutPoint) null, transaction.getValue().add(transaction.getValue())),
                            new TransactionInput((TransactionOutPoint) null, transaction.getValue().add(transaction.getValue().multiply(BigDecimal.valueOf(0.5))))
                    })
                    // Set some outputs
                    .setTxnOutputs(new TransactionOutput[] {
                            new TransactionOutput(CryptoHasher.generateWalletAddress(this.walletIndexer.getCurrentWallet().getPublicKey()), transaction.getValue().multiply(BigDecimal.valueOf(0.33))),
                            new TransactionOutput(CryptoHasher.hashByteArray(transaction.getDestinationAddress()), transaction.getValue())
                    })
                    .build();
            this.walletIndexer.getCurrentWallet().signTxn(mempoolTxn);
        } else {
            throw new TransactionException("The current wallet's address does not match the origin address of the submitted transaction.");
        }
        // add the newly created txn to the mempool
        this.mempool.putTransaction(mempoolTxn);
        // need to broadcast the mempool transaction.
        MempoolTransactionPayload txnPayload = MempoolTransactionPayload.Builder.builder()
                .buildFromMempool(mempoolTxn);
        MempoolTransactionMessage txnMessage = MempoolTransactionMessage.Builder.builder()
                .setTxnCount(1)
                .setTxns(new MempoolTransactionPayload[]{txnPayload})
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
     * Returns the wallet names.
     *
     * @return
     */
    public List<Pair<String, byte[]>> getWalletNames() {
        return this.walletIndexer.getAllWalletNames();
    }

    /**
     * Returns the currently loaded wallet.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public List<Wallet> getWallet(boolean allWallets) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        if(allWallets) {
            return this.walletIndexer.getAllWallets();
        }
        return List.of(this.walletIndexer.getCurrentWallet());
    }

    /**
     * Create a new wallet. Returns the newly created wallet.
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     */
    public Wallet createWallet(String walletLabel) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        logger.info("Generating new wallet...");
        Wallet wallet = this.walletIndexer.createNewWallet(walletLabel);
        logger.info("New wallet generated with address: {}", CryptoHasher.humanReadableHash(wallet.getAddress()));
        this.walletIndexer.switchCurrentWallet(wallet);
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
    public Wallet selectWallet(byte[] walletAddress) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        logger.debug("Selecting a wallet...");
        Wallet wallet = this.walletIndexer.getWallet(walletAddress);
        this.walletIndexer.switchCurrentWallet(wallet);
        logger.info("Wallet selected with address: {}", CryptoHasher.humanReadableHash(wallet.getAddress()));
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
        Wallet wallet = this.walletIndexer.getWallet(CryptoHasher.hashByteArray(walletAddress));
        this.walletIndexer.switchCurrentWallet(wallet);
        logger.info("Wallet selected with address: {}", CryptoHasher.humanReadableHash(wallet.getAddress()));
        return wallet;
    }

    // test code for testing messaging connections
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
        Transaction txn = Transaction.Builder.builder()
                .setOriginPublicKey(this.walletIndexer.getCurrentWallet().getPublicKey())
                .setDestinationAddress("6ad28d3fda4e10bdc0aaf7112f7818e181defa7e")
                .build();
        this.walletIndexer.getCurrentWallet().signTxn(txn);
        logger.info("signing...");
        logger.info(String.valueOf(txn));
        Signature ecdsaVerify = Signature.getInstance(CryptoKeyGenerator.getSignatureAlgorithm());
        ecdsaVerify.initVerify(this.walletIndexer.getCurrentWallet().getPublicKey());
        ecdsaVerify.update(txn.getTxnHash());
        logger.info(String.valueOf(ecdsaVerify.verify(txn.getSignature())));
        logger.info("done");
    }

    public void testMiningBlock() throws Exception {
        this.blockMiner.mineBlocks();
    }

    /**
     * Returns a most recent block response.
     *
     * @return
     */
    public void mineBlock() throws Exception {
        logger.trace("In mineBlock");
        this.blockMiner.mineBlocks();
    }

}
