package org.yggdrasil.node.network.exceptions;

/**
 * The invalid handshake exception is used for describing a failing
 * opening of a connection.
 *
 * @since 0.0.12
 * @author nathanielbunch
 */
public class InvalidInitialHandshakeException extends RuntimeException {

    public InvalidInitialHandshakeException(String reason) {
        super(reason);
    }

}
