package org.yggdrasil.node.network.messages.enums;

/**
 * The NetworkType is an identifying enum for
 * the test or main network message types so that
 * the different networks are not confused.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public enum NetworkType {

    MAIN_NET("MAIN"),
    TEST_NET("TEST");

    private final String value;
    private static final NetworkType[] values = new NetworkType[]{
            MAIN_NET,
            TEST_NET
    };

    NetworkType(String value) {
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

    public boolean containsValue(char[] value) {
        return this.value.contentEquals(String.valueOf(value));
    }

    public static NetworkType getByValue(char[] messageValue) {
        for(NetworkType nt : values) {
            if(nt.containsValue(messageValue)){
                return nt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
