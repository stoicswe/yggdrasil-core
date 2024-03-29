package org.yggdrasil.node.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.yggdrasil.node.network.messages.enums.NetworkType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

/**
 * The node config contains customizations that are set
 * from the application.yml.
 *
 * @since 0.0.8
 * @author nathanielbunch
 *
 */
@Profile("!test")
@Configuration
public class NodeConfig {

    private static final Logger logger = LoggerFactory.getLogger(NodeConfig.class);

    public final String _CURRENT_DIRECTORY = System.getProperty("user.dir") + "/.yggdrasil";
    public final String _CHAIN_DATA_DIRECTORY =  _CURRENT_DIRECTORY + "/blockchain";
    public final String _PEER_DATA_DIRECTORY =  _CURRENT_DIRECTORY +"/peers";
    public final String _FILE_EXTENSION = ".0x";

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
    @Value("${blockchain.p2p.peer-records}")
    private Integer peerRecordLimit;
    @Value("${blockchain.p2p.connection-timeout: 30000}")
    private Integer connectionTimeout;

    // Types: full, archival, relay
    // full = all services available
    // archival = only store new blocks
    // relay = only relay new information, do not store anything
    // TODO: Implement this switch, with logic to have only a specific ratio of relay to full or archival nodes
    @Value("${blockchain.client.mode: full}")
    private String mode;

    private Integer protocolVersion = 1;

    @PostConstruct
    public void init() throws UnknownHostException, SocketException {
        this.nodeIndex = UUID.randomUUID();
        // Setup save locations
        if(!Files.exists(Path.of(_CURRENT_DIRECTORY))) {
            logger.info("Creating Yggdrasil data directories");
            new File(_CURRENT_DIRECTORY).mkdir();
            logger.info("Created {}", _CURRENT_DIRECTORY);
            if(!Files.exists(Path.of(_CHAIN_DATA_DIRECTORY))) {
                new File(_CHAIN_DATA_DIRECTORY);
                logger.info("Created {}", _CHAIN_DATA_DIRECTORY);
            }
            if(!Files.exists(Path.of(_PEER_DATA_DIRECTORY))) {
                new File(_PEER_DATA_DIRECTORY).mkdir();
                logger.info("Created {}", _PEER_DATA_DIRECTORY);
            }
        }
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            // Set the nodes IP as seen on the local network
            // Will require port forward in order to open across internet
            this.nodeIp = InetAddress.getByName(socket.getLocalAddress().getHostAddress());
            logger.debug("Current node ip address: {}", nodeIp);
            logger.info("This node's identification: {}", String.format("%s-[%s]", this.nodeName, this.getNodeIdentifier()));
        }
    }

    public String getNodeIdentifier() {
        return String.format("%s", this.getNodeIndex().toString());
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

    public Integer getPeerRecordLimit() {
        return peerRecordLimit;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Integer getProtocolVersion() {
        return protocolVersion;
    }

    public String getMode() {
        return mode;
    }
}
