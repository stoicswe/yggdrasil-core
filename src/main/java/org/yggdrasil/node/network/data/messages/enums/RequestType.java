package org.yggdrasil.node.network.data.messages.enums;

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
