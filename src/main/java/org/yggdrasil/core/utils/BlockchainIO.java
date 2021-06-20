package org.yggdrasil.core.utils;

import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.chain.Blockchain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;

/**
 * The BlockchainIO component is used for writing block data to the
 * hard disk.
 *
 * @since 0.0.7
 * @author nathanielbunch
 */
@Profile("!test")
@Component
public class BlockchainIO {

    private final Logger logger = LoggerFactory.getLogger(BlockchainIO.class);
    private final String _CURRENT_DIRECTORY = System.getProperty("user.dir") + "/.yggdrasil/blockchain";
    private final String _FILE_EXTENSION = ".0x";

    @PostConstruct
    public void init() throws Exception {
        if(!Files.exists(Path.of(_CURRENT_DIRECTORY))) {
            new File(_CURRENT_DIRECTORY).mkdir();
        }
    }

    public void dumpChain(Blockchain blockchain) throws IOException {
        Block[] blocks = blockchain.getBlocks();
        for(Block b : blocks) {
            this.writeBlock(b);
        }
    }

    /**
     * Function for writing blocks to the disk.
     *
     * @param currentBlock
     * @throws IOException
     */
    public void writeBlock(Block currentBlock) throws IOException {
        logger.debug("Writing new block to storage: {}", currentBlock.toString());
        try(FileOutputStream currentBlockFile = new FileOutputStream(new File(_CURRENT_DIRECTORY + "/" + currentBlock.toString() + "_" + currentBlock.getTimestamp().toEpochSecond() + _FILE_EXTENSION))) {
            try(ObjectOutputStream currentBlockObject = new ObjectOutputStream(currentBlockFile)) {
                currentBlockObject.writeObject(currentBlock);
            }
        }
        logger.debug("Block written successfully.");
    }

    /**
     * Function for reading blocks from the disk.
     *
     * @param zonedDateTime
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Block readBlock(ZonedDateTime zonedDateTime) throws IOException, ClassNotFoundException {
        logger.debug("Reading block from storage...");
        FileInputStream currentBlockFile = new FileInputStream(new File(_CURRENT_DIRECTORY + "/" + zonedDateTime.toEpochSecond() + _FILE_EXTENSION));
        ObjectInputStream currentBlockObject = new ObjectInputStream(currentBlockFile);
        Block archiveBlock = (Block) currentBlockObject.readObject();
        logger.debug("Block read successfully: {}", archiveBlock.toString());
        return archiveBlock;
    }

}
