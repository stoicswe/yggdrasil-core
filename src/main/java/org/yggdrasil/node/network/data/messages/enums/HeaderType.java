package org.yggdrasil.node.network.data.messages.enums;

public enum HeaderType {

    BLOCK_HEADER("BLKH".toCharArray()),
    TXN_HEADER("TXNH".toCharArray());

    private char[] value;

    HeaderType(char[] value) {
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
