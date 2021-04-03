package org.yggdrasil.node.network.messages;

import java.io.Serializable;

/**
 * Interface for message payload so that it can be genericized.
 *
 * @since 0.0.10
 * @author nathanielbunch
 *
 */
public interface MessagePayload extends Serializable {

    byte[] getDataBytes();

}
