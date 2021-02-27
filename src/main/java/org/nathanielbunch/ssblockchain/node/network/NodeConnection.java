package org.nathanielbunch.ssblockchain.node.network;

import org.nathanielbunch.ssblockchain.node.network.data.BlockMessageHandler;
import org.nathanielbunch.ssblockchain.node.network.data.Message;
import org.nathanielbunch.ssblockchain.node.network.data.MessageIdentifier;
import org.nathanielbunch.ssblockchain.node.network.data.TransactionMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class NodeConnection implements Runnable {

    Logger logger = LoggerFactory.getLogger(NodeConnection.class);

    private Socket nodeSocket;

    public NodeConnection(Socket node){
        this.nodeSocket = node;
    }

    public Socket getNodeSocket() {
        return this.nodeSocket;
    }

    @Override
    public void run() {
        while(nodeSocket.isConnected()){
            try (InputStream ms = nodeSocket.getInputStream()) {
                try (ObjectInputStream os = new ObjectInputStream(ms)) {
                    Message m = (Message) os.readObject();
                    if(m.getIdentifier() != null) {
                        if(MessageIdentifier.BLOCK_MESSAGE.equals(m.getIdentifier())){
                            new Thread(new BlockMessageHandler(m)).start();
                        }
                        if(MessageIdentifier.TRANSACTIONAL_MESSAGE.equals(m.getIdentifier())){
                            new Thread(new TransactionMessageHandler(m)).start();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Socket input stream read failed with exception: {}", e.getLocalizedMessage());
            }
        }
    }
}
