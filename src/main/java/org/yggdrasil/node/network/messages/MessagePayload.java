package org.yggdrasil.node.network.messages;

/**
 * Interface for message payload so that it can be genericized.
 *
 * @since 0.0.10
 * @author nathanielbunch
 *
 */
public interface MessagePayload {

    byte[] getDataBytes();

}
