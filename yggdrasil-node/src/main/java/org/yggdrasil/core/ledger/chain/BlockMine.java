package org.yggdrasil.core.ledger.chain;

import org.apache.commons.lang3.ArrayUtils;
import org.openjdk.jol.info.GraphLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.ledger.transaction.TransactionInput;
import org.yggdrasil.core.ledger.transaction.TransactionOutPoint;
import org.yggdrasil.core.ledger.transaction.TransactionOutput;
import org.yggdrasil.core.ledger.wallet.WalletIndexer;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.core.utils.CryptoKeyGenerator;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.NodeConfig;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.Messenger;
import org.yggdrasil.node.network.messages.enums.CommandType;
import org.yggdrasil.node.network.messages.payloads.BlockMessage;
import org.yggdrasil.node.network.messages.payloads.TransactionPayload;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BlockMine {

    private final Logger logger = LoggerFactory.getLogger(BlockMine.class);
    private final Integer _MAX_BLOCK_SIZE = 2048;
    private final Integer _BLOCK_REWARD_HALVING = 210000;

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private Messenger messenger;
    @Autowired
    private Mempool mempool;
    @Autowired
    private Blockchain blockchain;
    @Autowired
    private WalletIndexer walletIndexer;

    private Signature signatureVerification;
    private Thread miningThread;
    protected boolean isMiningState = false;

    @PostConstruct
    private void init() throws NoSuchAlgorithmException {
        this.isMiningState = false;
        this.signatureVerification = Signature.getInstance(CryptoKeyGenerator.getSignatureAlgorithm());
    }

    public void startMining() {
        if(this.miningThread.isAlive()) {
            this.isMiningState = false;
        }
        // erase old thread
        this.miningThread = null;
        // make a new thread with the runner to mine
        this.miningThread = new Thread();
        this.isMiningState = true;
        this.miningThread.start();
    }

    public void stopMining() {
        // stop the thread for mining and destroy it
        if(this.miningThread.isAlive()){
            this.isMiningState = false;
        }
    }

    // method that will be called by the runner.
    public void mineBlocks() throws Exception {
        logger.info("Mining new block...");
        // make a blocking check here for memTxns size > 10.
        List<Transaction> memTxns = this.mempool.getTransaction(_MAX_BLOCK_SIZE);
        // The txns selected to be in this block.
        Set<Transaction> bTxnCandidates;
        // need to check what are the most valuable transactions and perform work on those
        // with some free transactions, a maximum of 10% of total work.
        logger.info("Selecting transactions to be included in the new block.");
        memTxns.sort(Comparator.comparing(Transaction::getValue));
        BigDecimal mV = memTxns.stream().map(Transaction::getValue).reduce(BigDecimal::add).orElse(BigDecimal.ONE).divide(BigDecimal.valueOf(memTxns.size()), RoundingMode.HALF_UP);
        logger.info("Median value of {} transactions to be evaluated: {}", memTxns.size(), mV);
        bTxnCandidates = memTxns.stream().filter(memTxnF -> memTxnF.getValue().compareTo(mV) > 0).collect(Collectors.toSet());
        int tenPercent = (int) Math.round(1.0*memTxns.size()*0.1);
        logger.info("{} high value transactions selected, with {} low value ones to be added.", bTxnCandidates.size(), tenPercent);
        // shifting the percent and size by one to avoid one-off errors
        bTxnCandidates.addAll(memTxns.subList(memTxns.size()-(tenPercent+1), memTxns.size()-1));
        // Get the last known block to reference in the new block
        // Should never return null, since there will always be a genesis block...
        Block lastBlock = this.blockchain.getLastBlock().orElse(null);
        // Transaction payloiad for including in the block message
        List<TransactionPayload> txnMessagePayloads = new ArrayList<>();
        List<Transaction> bTxnsInvalid = new ArrayList<>();
        logger.info("Validating block candidates");
        for(Transaction txn : bTxnCandidates) {
            boolean txnIsValid = false;
            // Validate every txn
            if(txn.isCoinbase()) {
                logger.info("Txn: {} is invalid, was free-floating coinbase.", txn);
                bTxnsInvalid.add(txn);
                txnIsValid = false;
            } else {
                // "Holy IF statements batman!" ~ Robin.

                // Uncomment this once the wallet is finished being coded...
                // this is commented for testing purposes.

                /*
                for (TransactionInput txnIn : txn.getTxnInputs()) {
                    TransactionOutPoint txnOutPt = txnIn.getTxnOutPt();
                    if(txnOutPt != null) {
                        Optional<Block> prevBlock = this.blockchain.getBlock(txnOutPt.getTxnHash());
                        if (prevBlock.isPresent()) {
                            Optional<Transaction> prevTxn = prevBlock.get().getTransaction(txnOutPt.getTxnHash());
                            if (prevTxn.isPresent()) {
                                if (CryptoHasher.isEqualHashes(CryptoHasher.hashByteArray(prevTxn.get().getDestinationAddress()), CryptoHasher.hashByteArray(txn.getOriginAddress()))) {
                                    if (CryptoHasher.isEqualHashes(CryptoHasher.hashByteArray(prevTxn.get().getDestinationAddress()), CryptoHasher.generateWalletAddress(txn.getOrigin()))) {
                                        for (TransactionOutput txnOut : prevTxn.get().getTxnOutPuts()) {
                                            if (txnOut.isMine(txn.getOrigin(), txn.getSignature())) {
                                                if (txnOut.getValue().compareTo(txnOutPt.getValue()) == 0) {
                                                    signatureVerification.initVerify(txn.getOrigin());
                                                    if (signatureVerification.verify(txn.getSignature())) {
                                                        logger.info("Txn: {} is valid.", txn);
                                                        txnIsValid = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                logger.info("Txn: {} is invalid, prevTxn == null", txn);
                                txnIsValid = false;
                                break;
                            }
                        } else {
                            logger.info("Txn: {} is invalid, prevBlock == null", txn);
                            txnIsValid = false;
                            break;
                        }
                    } else {
                        logger.info("Txn: {} is invalid, txnOutPt == null", txn);
                        txnIsValid = false;
                        break;
                    }
                }*/
                txnIsValid = true;
            }
            if(txnIsValid) {
                logger.info("Txn: {} is valid.", txn);
                // Once the txn is validated, add to the transactionPayload
                TransactionPayload txnP = TransactionPayload.Builder.builder()
                        .buildFromTxn(txn);
                txnMessagePayloads.add(txnP);
            } else {
                bTxnsInvalid.add(txn);
            }
        }
        if(bTxnsInvalid.size() > 0) {
            bTxnCandidates.removeAll(bTxnsInvalid);
        }
        // Generate a coinbase transaction to be included in the block
        // verify that only one can be added to the block
        logger.info("Generating coinbase txn.");
        Transaction coinbase = Transaction.Builder.builder()
                .setTimestamp(DateTimeUtil.getCurrentTimestamp())
                .setOriginAddress(null)
                .setDestinationAddress(this.walletIndexer.getCurrentWallet().getHumanReadableAddress())
                .setTxnInputs(new TransactionInput[]{new TransactionInput((TransactionOutPoint) null, this.calculateBlockReward(lastBlock.getBlockHeight()))})
                .setTxnOutputs(new TransactionOutput[]{new TransactionOutput(CryptoHasher.hashByteArray(this.walletIndexer.getCurrentWallet().getHumanReadableAddress()), this.calculateBlockReward(lastBlock.getBlockHeight()))})
                .build();
        bTxnCandidates.add(coinbase);
        logger.info("New block will contain {} total txns.", bTxnCandidates.size());
        // Merkle root variable for including in the block
        // as part of generating the merkleRoot, find a way to add merkle branch
        // to each txn to connect it back to the block
        byte[] merkleRoot = generateMerkleTree(new ArrayList<>(bTxnCandidates));
        // Compile the block
        BlockHeader header = BlockHeader.Builder.builder()
                .setVersion(Blockchain._VERSION)
                .setPreviousBlockHash(lastBlock.getBlockHash())
                .setMerkleRoot(merkleRoot)
                .setTime(DateTimeUtil.getCurrentTimestamp())
                .setDiff(lastBlock.getHeader().getDiff())
                .setNonce(0)
                .build();
        Block newBlock = Block.Builder.newBuilder()
                .setBlockHeader(header)
                .setData(new ArrayList<>(bTxnCandidates))
                .build();
        // Perform the proof of work
        newBlock = this.proofOfWork(newBlock, lastBlock.getHeader().getDiff());
        // add the block to the blockchain after performing PoW
        this.blockchain.addBlock(newBlock);
        // Now that the work is done, we can remove the txns included in the block
        // from the mempool, so we do not compute them again
        memTxns.removeAll(bTxnCandidates);
        logger.debug("Dumping low-value transactions back into the mempool for later block.");
        // txns that we did not use (low val ones) can be put back into the mempool
        this.mempool.putAllTransaction(memTxns);
        logger.info("Added new block to the chain: {}", newBlock);
        // the new block can now be transmitted to the other nodes
        // when receiving these, other noes can validate the new block
        BlockMessage blockMessage = BlockMessage.Builder.builder()
                .setVersion(Blockchain._VERSION)
                .setPreviousBlock(newBlock.getHeader().getPreviousBlockHash())
                .setMerkleRoot(newBlock.getHeader().getMerkleRoot())
                .setTimestamp((int) newBlock.getHeader().getEpochTime())
                .setDiff(newBlock.getHeader().getDiff())
                .setNonce(newBlock.getHeader().getNonce())
                .setTxnCount(newBlock.getTxnCount())
                .setTxnPayloads(txnMessagePayloads.toArray(TransactionPayload[]::new))
                .build();
        Message message = Message.Builder.builder()
                .setNetwork(nodeConfig.getNetwork())
                .setRequestType(CommandType.INVENTORY_PAYLOAD)
                .setMessagePayload(blockMessage)
                .setPayloadSize(BigInteger.valueOf(GraphLayout.parseInstance(blockMessage).totalSize()))
                .setChecksum(CryptoHasher.hash(blockMessage))
                .build();
        this.messenger.sendBroadcastMessage(message);
        logger.info("New block {} has been forwarded to other nodes.", newBlock);
    }

    private byte[] generateMerkleTree(List<Transaction> txns) throws NoSuchAlgorithmException {
        if(txns.size()%2 != 0) {
            // duplicate the last item in the list
            // for adding to the merkle tree to ensure
            // there is not an issue with the recursive call
            txns.add(txns.get(txns.size()-1));
        }
        byte[] temp = new byte[0];
        if(txns.size() == 2) {
            temp = appendBytes(temp, txns.get(0).getTxnHash());
            temp = appendBytes(temp, txns.get(1).getTxnHash());
            return CryptoHasher.dhash(temp);
        }
        if(txns.size() == 1) {
            temp = appendBytes(temp, txns.get(0).getTxnHash());
            temp = appendBytes(temp, txns.get(0).getTxnHash());
            return CryptoHasher.dhash(temp);
        }
        // pass first 1/2 and second 1/2
        // need to test to make sure this never has issues
        // or misses any txns...
        return CryptoHasher.dhash(appendBytes(generateMerkleTree(txns.subList(0, (txns.size()/2)-1)), generateMerkleTree(txns.subList((txns.size()/2), txns.size()-1))));
    }

    private Block proofOfWork(Block currentBlock, int difficulty) throws Exception {
        // Get the txn data for sorting
        List<Transaction> blockTransactions = currentBlock.getData();
        // first we want to sort by txn timestamp
        blockTransactions.sort(Comparator.comparing(Transaction::getTimestamp));
        // then we want to move the coinbase txn to the top of the new block
        blockTransactions.sort(Comparator.comparing(Transaction::isCoinbase));
        // Rebuild the block
        BlockHeader header = BlockHeader.Builder.builder()
                .setVersion(Blockchain._VERSION)
                .setPreviousBlockHash(currentBlock.getHeader().getPreviousBlockHash())
                .setMerkleRoot(currentBlock.getHeader().getMerkleRoot())
                .setTime(DateTimeUtil.getCurrentTimestamp())
                .setDiff(currentBlock.getHeader().getDiff())
                .setNonce(currentBlock.getHeader().getNonce())
                .build();
        Block sortedBlock = Block.Builder.newBuilder()
                .setBlockHeader(header)
                .setData(blockTransactions)
                .build();
        // This output is mostly for testing, was curious about adding bytes together *shrugs*
        logger.info("Initial new block hash value: {}", this.sumBytes(sortedBlock.getBlockHash()));
        // Output the difficulty (num of proceeding zeros)
        logger.info("Trying to beat difficulty: {}", difficulty);
        // Resulting hash string to beat
        String prefixString = new String(new char[difficulty]).replace('\0', '0');
        while (!sortedBlock.toString().substring(0, difficulty).equals(prefixString)) {
            // Every 100 nonce updates, we want to output a progress log
            // so that the miner can see something happening in their computer
            if(sortedBlock.getHeader().getNonce() % 500 == 0) {
                logger.info("Working block hash: {}", sortedBlock);
            }
            // move that nonce!
            // there is a chance I might need to add a
            // nonce to the coinbase txn as well to allow
            // for more creative solving, but maybe later
            // TODO: Make a new block by copying data / create a new header
            //sortedBlock.incrementNonce();
        }
        // Ooooo shiny new block, much wow!
        return sortedBlock;
    }

    private BigDecimal calculateBlockReward(BigInteger blockHeight) {
        BigInteger coin = BigInteger.valueOf(50);
        BigDecimal factor = BigDecimal.valueOf(blockHeight.divide(BigInteger.valueOf(210000)).longValue()).setScale(2, RoundingMode.HALF_UP);
        if(factor.compareTo(BigDecimal.ZERO) == 0) {
            factor = BigDecimal.ONE;
        }
        return BigDecimal.valueOf(coin.longValue()).multiply(factor);
    }

    // This was just for testing an idea
    // might leave here for awhile
    private int sumBytes(byte[] bytes) {
        int sum = 0;
        for(byte b : bytes) {
            sum += b;
        }
        return sum;
    }

    // Some helper function for sum(hash, hash)
    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

}
