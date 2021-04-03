package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The node runner watches for connection attempts and calls the startListening
 * function.
 *
 * @since 0.0.8
 * @author nathanielbunch
 *
 */
public class NodeRunner implements Runnable {

    Logger logger = LoggerFactory.getLogger(NodeRunner.class);

    private Node node;

    public NodeRunner(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        try {
            this.node.establishConnections();
            logger.info("Opening listener.");
            this.node.startListening();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Cannot start listening: {}", e.getMessage());
        }
    }

}
