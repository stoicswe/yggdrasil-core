package org.yggdrasil.node.network.messages.enums;

/**
 * The GetDataType is an identifying enum for getData requests.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public enum GetDataType {

    BLOCKCHAIN("BLKC"),
    BLOCK("BLKS"),
    TRANSACTION("TXNS"),
    MEMPOOL("MMPL");

    private String value;

    GetDataType(String value) {
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
