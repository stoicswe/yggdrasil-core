package org.yggdrasil.node.network.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.node.network.Node;
import org.yggdrasil.node.network.messages.validators.MessageValidator;

/**
 * The Messenger component will handle the messages from other nodes
 * and the sending of messages to other nodes.
 *
 * @since 0.0.11
 * @author nathanielbunch
 */
@Component
public class Messenger {

    private final Logger logger = LoggerFactory.getLogger(Messenger.class);

    @Autowired
    private Node node;
    @Autowired
    private MessageValidator validator;

    public void handleMessage(Message message) {
        logger.trace("In handleMessage.");
        try {
            this.validator.isValidMessage(message);
            logger.debug("Handling valid message.");
            // do things with the message
        } catch (Exception e) {
            logger.warn("Error while processing message: [{}] -> [{}]", message.toString(), e.getMessage());
        }
    }

}
