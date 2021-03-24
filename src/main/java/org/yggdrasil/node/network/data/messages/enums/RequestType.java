package org.yggdrasil.node.network.data.messages.enums;

/**
 * The RequestType is an identifying enum for
 * message headers and helps nodes handle data
 * properly.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public enum RequestType {

    GET_DATA("GDAT".toCharArray()),
    DATA_RESP("RDAT".toCharArray()),
    GET_ADDR("GADD".toCharArray()),
    ADDR_RESP("RADD".toCharArray()),
    PING("PING".toCharArray()),
    PONG("PONG".toCharArray()),
    HANDSHAKE_OFFR("WVTO".toCharArray()),
    HANDSHAKE_RESP("WVBK".toCharArray());

    private char[] value;

    RequestType(char[] value) {
        this.value = value;
    }

    public char[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
