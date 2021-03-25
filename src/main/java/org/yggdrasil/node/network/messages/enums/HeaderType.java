package org.yggdrasil.node.network.messages.enums;

/**
 * The HeaderType is an identifying enum for header messages
 * being requested or received.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public enum HeaderType {

    BLOCK_HEADER("BLKH"),
    TXN_HEADER("TXNH");

    private String value;

    HeaderType(String value) {
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
