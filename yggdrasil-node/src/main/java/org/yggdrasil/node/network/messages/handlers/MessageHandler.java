package org.yggdrasil.node.network.messages.handlers;

import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.runners.NodeConnection;

/**
 * The message handler interface ensures that all message handlers
 * will be implemented similarly.
 *
 * @param <MessagePayload>
 * @since 0.0.13
 * @author nathanielbunch
 */
public interface MessageHandler<MessagePayload> {

    public void handleMessagePayload(MessagePayload payload, NodeConnection nodeConnection) throws Exception;

}
