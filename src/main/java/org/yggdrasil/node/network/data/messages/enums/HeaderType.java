package org.yggdrasil.node.network.data.messages.enums;

/**
 * The HeaderType is an identifying enum for header messages
 * being requested or received.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
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
