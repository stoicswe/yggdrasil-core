package org.yggdrasil.node.network.data.messages.enums;

/**
 * The NetworkType is an identifying enum for
 * the test or main network message types so that
 * the different networks are not confused.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public enum NetworkType {

    MAIN_NET("MAIN".toCharArray()),
    TEST_NET("TEST".toCharArray());

    private final char[] value;

    NetworkType(char[] value) {
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
