package org.yggdrasil.node.network.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.utils.DateTimeUtil;
import org.yggdrasil.node.network.runners.MessagePoolRunner;
import org.yggdrasil.node.network.runners.NodeConnection;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * This class is used for storage of messages that require some sort of response. If a response
 * is not received in specific time, the node can react to that.
 *
 * @since 0.0.14
 * @author nathanielbunch
 */
@Component
public class MessagePool {

    private static final Logger logger = LoggerFactory.getLogger(MessagePool.class);

    private HashMap<byte[], ExpiringMessageRecord<ZonedDateTime, String, Message>> messagePool;

    @PostConstruct
    private void init() {
        this.messagePool = new HashMap<>();
    }

    public void putMessage(Message message, NodeConnection nodeConnection) {
        logger.trace("In putMessage");
        this.messagePool.put(message.getChecksum(), new ExpiringMessageRecord<>(DateTimeUtil.getCurrentTimestamp(), nodeConnection.getNodeIdentifier(), message));
        logger.trace("New message added to the message pool: {}", message.toString());
    }

    public ExpiringMessageRecord<ZonedDateTime, String, Message> getMessage(byte[] checkSum) {
        logger.trace("In getMessage");
        return messagePool.get(checkSum);
    }

    public void removeMessage(byte[] checkSum) {
        logger.trace("In removeMessage");
        this.messagePool.remove(checkSum);
    }

    public ExpiringMessageRecord[] checkMessages() {
        return (ExpiringMessageRecord[]) this.messagePool.values().stream().filter(message -> ChronoUnit.MINUTES.between(message.timestamp, DateTimeUtil.getCurrentTimestamp()) > 1).toArray();
    }

}
