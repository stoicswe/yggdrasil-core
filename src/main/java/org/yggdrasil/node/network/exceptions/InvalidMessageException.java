package org.yggdrasil.node.network.exceptions;

/**
 * The invalid message exception is used for passing certain types
 * of messages for specific scenarios.
 *
 * @since 0.0.11
 * @author nathanielbunch
 */
public class InvalidMessageException extends RuntimeException {

    public InvalidMessageException(String reason) {
        super(reason);
    }

}
