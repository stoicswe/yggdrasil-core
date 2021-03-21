package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NodeRunner implements Runnable {

    Logger logger = LoggerFactory.getLogger(NodeRunner.class);

    private Node node;

    public NodeRunner(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        try {
            this.node.startListening();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Cannot start listening: {}", e.getMessage());
        }
    }

}
