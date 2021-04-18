package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.yggdrasil.node.network.messages.enums.NetworkType;

import javax.annotation.PostConstruct;
import java.net.*;
import java.util.Locale;
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

    private static final Logger logger = LoggerFactory.getLogger(NodeConfig.class);

    private UUID nodeIndex;
    private InetAddress nodeIp;
    @Value("${blockchain.p2p.node-name}")
    private String nodeName;
    @Value("${blockchain.network}")
    private String network;
    @Value("${blockchain.p2p.port}")
    private Integer port;
    @Value("${blockchain.p2p.peers}")
    private String[] peers;
    @Value("${blockchain.p2p.active-connections}")
    private Integer activeConnections;
    @Value("${blockchain.p2p.connection-timeout: 30000}")
    private Integer connectionTimeout;

    private Integer protocolVersion = 1;

    @PostConstruct
    public void init() throws UnknownHostException, SocketException {
        this.nodeIndex = UUID.randomUUID();
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            // Set the nodes IP as seen on the local network
            // Will require port forward in order to open across internet
            this.nodeIp = InetAddress.getByName(socket.getLocalAddress().getHostAddress());
            logger.debug("Current node ip address: {}", nodeIp);
            logger.info("This node's identification: {}", this.getNodeIdentifier());
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

    public String[] getPeers() {
        return peers;
    }

    public NetworkType getNetwork() {
        return NetworkType.getByValue(network.toUpperCase(Locale.ROOT).toCharArray());
    }

    public Integer getPort() {
        return port;
    }

    public Integer getActiveConnections() {
        return activeConnections;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getProtocolVersion() {
        return protocolVersion;
    }
}
