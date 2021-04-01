package org.yggdrasil.node.network.messages.handlers;

import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;

/**
 * The message handler interface ensures that all message handlers
 * will be implemented similarly.
 *
 * @param <MessagePayload>
 * @since 0.0.13
 * @author nathanielbunch
 */
public interface MessageHandler<MessagePayload> {

    public org.yggdrasil.node.network.messages.MessagePayload handleMessagePayload(MessagePayload payload);

}
