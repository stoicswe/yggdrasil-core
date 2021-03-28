package org.yggdrasil.node.network.messages.enums;

/**
 * The RequestType is an identifying enum for
 * message headers and helps nodes handle data
 * properly.
 *
 * @since 0.0.10
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
    private static final RequestType[] values = new RequestType[]{GET_DATA, DATA_RESP, GET_ADDR, ADDR_RESP, PING, PONG, HANDSHAKE_OFFR, HANDSHAKE_RESP};

    RequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public char[] getMessageValue() {
        return value.toCharArray();
    }

    public boolean containsValue(char[] value) {
        return this.value.contentEquals(String.valueOf(value));
    }

    public static RequestType equals(char[] messageValue) {
        for(RequestType rt : values) {
            if(rt.containsValue(messageValue)){
                return rt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
