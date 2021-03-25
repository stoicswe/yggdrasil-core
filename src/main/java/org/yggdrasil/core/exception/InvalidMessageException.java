package org.yggdrasil.core.exception;

public class InvalidMessageException extends RuntimeException {

    public InvalidMessageException(String reason) {
        super(reason);
    }

}
