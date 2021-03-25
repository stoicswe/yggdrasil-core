package org.yggdrasil.node.network;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.net.*;
import java.util.UUID;

/**
 * The node config contains customizations that are set
 * from the application.yaml.
 *
 * @since 0.0.8
 * @author nathanielbunch
 *
 */
@Profile("!test")
@Configuration
public class NodeConfig {

    private UUID nodeIndex;
    private InetAddress nodeIp;
    @Value("${blockchain.network}")
    private String[] networks;
    @Value("${blockchain.p2p.port}")
    private Integer port;
    @Value("${blockchain.p2p.node-name}")
    private String nodeName;
    @Value("${blockchain.p2p.active-connections}")
    private Integer activeConnections;
    @Value("${blockchain.p2p.timeout}")
    private Integer timeout;

    @PostConstruct
    public void init() throws UnknownHostException, SocketException {
        this.nodeIndex = UUID.randomUUID();
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            // Set the nodes IP as seen on the local network
            // Will require port forward in order to open across internet
            this.nodeIp = InetAddress.getByName(socket.getLocalAddress().getHostAddress());
        }
    }

    public String getNodeIdentifier() {
        return String.format("%s-[%s]", this.getNodeName(), this.getNodeIndex().toString());
    }

    public UUID getNodeIndex() {
        return nodeIndex;
    }

    public String getNodeName() {
        return nodeName;
    }

    public InetAddress getNodeIp() {
        return nodeIp;
    }

    public String[] getNetworks() {
        return networks;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getActiveConnections() {
        return activeConnections;
    }

    public Integer getTimeout() {
        return timeout;
    }
}
