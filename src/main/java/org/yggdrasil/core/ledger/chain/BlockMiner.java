package org.yggdrasil.core.ledger.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.yggdrasil.core.ledger.Mempool;

public class BlockMiner {

    private final Logger logger = LoggerFactory.getLogger(BlockMiner.class);

    @Autowired
    private Mempool mempool;

    /*public Block buildGenesisBlock() throws NoSuchAlgorithmException {
        return new Block();
    }

    // Build a block and generate a merkle tree.
    // Build out a validation process for verifying the signatures of the hashes involved
    // to initiate the transactions. Also, add logic for checking
    // txns in the mempool to see if those contain a fee that is
    // greater than a moving average in the mempool. If those fees are
    // greater than average, then process those first.

    public Block mineBlock(){
        Transaction txn = Transaction.Builder.Builder()
                .setOrigin()
                .setDestination()
                .setFee()
                .setSignature()
                .build();
    }

    public Block buildFromBlockHeaderMessage(BlockHeaderPayload blockHeaderPayload) {
        this.previousBlock = blockHeaderPayload.getPrevHash();
        this.index = UUID.fromString(String.valueOf(blockHeaderPayload.getIndex()));
        this.timestamp = DateTimeUtil.fromMessageTimestamp(blockHeaderPayload.getTimestamp());
        this.nonce = blockHeaderPayload.getNonce();
        this.blockHash = blockHeaderPayload.getHash();
        this.data = new ArrayList<>();
        Block blck = new Block(this);
        blck.setBlockHash(this.blockHash);
        return blck;
    }

    public Block buildFromBlockMessage(BlockMessage blockMessage) {
        this.timestamp = DateTimeUtil.fromMessageTimestamp(blockMessage.getTimestamp());
        this.nonce = blockMessage.getNonce();
        this.blockHash = blockMessage.getBlockHash();
        this.previousBlock = blockMessage.getPreviousBlockHash();
        List<Transaction> data = new ArrayList<>();
        for(TransactionPayload txnPayload : blockMessage.getTxnPayloads()){
            data.add(Transaction.Builder.Builder().buildFromMessage(txnPayload));
        }
        this.data = data;
        Block blck = new Block(this);
        if(CryptoHasher.compareHashes(this.blockHash, blck.blockHash)){
            logger.debug("Locally generated blockhash matched incoming blockhash from payload");
        } else {
            logger.debug("Locally generated blockhash did not match, manually setting blockhaash from payload");
            blck.setBlockHash(this.blockHash);
        }
        blck.setSignature(blockMessage.getSignature());
        blck.setValidator(blockMessage.getValidator());
        return blck;
    }*/

}
