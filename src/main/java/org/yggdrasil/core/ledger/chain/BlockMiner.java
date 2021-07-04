package org.yggdrasil.core.ledger.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.ledger.Mempool;

@Component
public class BlockMiner {

    private final Logger logger = LoggerFactory.getLogger(BlockMiner.class);
    private final Integer _PREFIX = 4;
    private final Integer _MAX_BLOCK_SIZE = 2048;

    @Autowired
    private Mempool mempool;
    @Autowired
    private Blockchain blockchain;

    

}
