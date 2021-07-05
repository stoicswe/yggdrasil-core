package org.yggdrasil.node.service;

import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.BlockMine;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.exceptions.TransactionException;
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
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionMessage;
import org.yggdrasil.node.network.messages.payloads.MempoolTransactionPayload;
import org.yggdrasil.node.network.messages.payloads.PingPongMessage;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Comparator;
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
                    // add the inputs and outputs to here
                    .setDestinationAddress(transaction.getDestinationAddress())
                    .build();
            this.walletIndexer.getCurrentWallet().signTransaction(mempoolTxn);
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
    public Wallet createWallet() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        logger.info("Generating new wallet...");
        Wallet wallet = this.walletIndexer.createNewWallet();
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
        this.walletIndexer.getCurrentWallet().signTransaction(txn);
        logger.info("signing...");
        logger.info(String.valueOf(txn));
        Signature ecdsaVerify = Signature.getInstance(CryptoKeyGenerator.getSignatureAlgorithm());
        ecdsaVerify.initVerify(this.walletIndexer.getCurrentWallet().getPublicKey());
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

        Transaction blockMineAward = Transaction.Builder.builder()
                .setOriginPublicKey(CryptoKeyGenerator.readPublicKeyFromBytes(CryptoHasher.hashByteArray("7c5ec4b1ad5bdfc593587f3a9d50327ede02076b")))
                .setDestinationAddress(this.walletIndexer.getCurrentWallet().getHumanReadableAddress())
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
