package org.yggdrasil.node.network.data.messages.enums;

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
