package org.yggdrasil.core.ledger.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.Messenger;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

@Component
public class BlockMiner {

    private final Logger logger = LoggerFactory.getLogger(BlockMiner.class);
    private final Integer _PREFIX = 4;
    private final Integer _MAX_BLOCK_SIZE = 2048;

    @Autowired
    private Messenger messenger;
    @Autowired
    private Mempool mempool;
    @Autowired
    private Blockchain blockchain;

    private Thread miningThread;
    protected boolean isMiningState = false;

    @PostConstruct
    private void init() {
        this.isMiningState = false;
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
    private void mineBlocks() {
        logger.info("Mining new block...");
        // need to check what are the most valuable transactions and perform work on those
        Block lastBlock = this.blockchain.getLastBlock().orElse(null);
    }

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
