package org.nathanielbunch.ssblockchain.node.network;

import org.nathanielbunch.ssblockchain.node.network.data.Message;
import org.nathanielbunch.ssblockchain.node.network.data.MessageIdentifier;
import org.nathanielbunch.ssblockchain.node.network.data.MessageSendHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

@Profile("!test")
@Component
public class Node {

    private Logger logger = LoggerFactory.getLogger(Node.class);

    @Autowired
    private NodeConfig nodeConfig;
    private ServerSocket serverSocket;
    private HashMap<String, NodeConnection> connectedNodes;

    @PostConstruct
    public void init() throws IOException, ClassNotFoundException {
        this.connectedNodes = new HashMap<>();
        this.serverSocket = new ServerSocket(nodeConfig.getPort(), 3, nodeConfig.getNodeIp());
        Thread nodeRunner = new Thread(new NodeRunner(this));
        nodeRunner.start();
    }

    public HashMap<String, NodeConnection> getConnectedNodes() {
        return this.connectedNodes;
    }

    public void startListening() throws IOException, ClassNotFoundException {
        Socket client;
        while(true){
            client = serverSocket.accept();
            if(nodeConfig.getActiveConnections() < connectedNodes.size()) {
                client.setKeepAlive(true);
                client.setSoTimeout(nodeConfig.getTimeout());
                InputStream bis = client.getInputStream();
                ObjectInputStream objIn = new ObjectInputStream(bis);
                Message m = (Message) objIn.readObject();
                if (m.getIdentifier().equals(MessageIdentifier.IDENTIFY_MESSAGE)) {
                    NodeConnection n = new NodeConnection(client);
                    this.connectedNodes.put(m.getSender(), n);
                    Thread nt = new Thread(n);
                    nt.start();
                }
            } else {
                client.close();
            }
        }
    }

    private void sendMessage(Message m) {
        for(String s : connectedNodes.keySet()){
            NodeConnection n = connectedNodes.get(s);
            if(n.getNodeSocket().isConnected()) {
                new MessageSendHandler(m, n.getNodeSocket()).run();
            } else {
                connectedNodes.remove(s);
            }
        }
    }
}
