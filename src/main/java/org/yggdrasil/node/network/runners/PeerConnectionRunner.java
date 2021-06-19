package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.Node;

import java.io.IOException;

/**
 * The peer connection runner will attempt to established past connections
 * to nodes that are saved.
 *
 * @since 0.0.14
 * @author nathanielbunch
 */
public class PeerConnectionRunner implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PeerConnectionRunner.class);
    private Node node;

    public PeerConnectionRunner(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        try {
            logger.info("Attempting to establish peer connections.");
            this.node.establishConnections();
        } catch (IOException e) {
            logger.error("Connections to peers failed: {}", e.getMessage());
        }
    }
}
