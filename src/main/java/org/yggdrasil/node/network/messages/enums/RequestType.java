package org.yggdrasil.node.network.messages.enums;

/**
 * The RequestType is an identifying enum for
 * message headers and helps nodes handle data
 * properly.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public enum RequestType {

    GET_DATA("GDAT"),
    DATA_RESP("RDAT"),
    GET_ADDR("GADD"),
    ADDR_RESP("RADD"),
    PING("PING"),
    PONG("PONG"),
    HANDSHAKE_OFFR("WVTO"),
    HANDSHAKE_RESP("WVBK");

    private String value;

    RequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public char[] getMessageValue() {
        return value.toCharArray();
    }

    public boolean equals(char[] messageValue) {
        return this.value.contentEquals(String.valueOf(messageValue));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
