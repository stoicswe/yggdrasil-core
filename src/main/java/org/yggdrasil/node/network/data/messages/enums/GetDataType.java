package org.yggdrasil.node.network.data.messages.enums;

/**
 * The GetDataType is an identifying enum for getData requests.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public enum GetDataType {

    BLOCKCHAIN("BLKC".toCharArray()),
    BLOCK("BLKS".toCharArray()),
    TRANSACTION("TXNS".toCharArray()),
    MEMPOOL("MMPL".toCharArray());

    private char[] value;

    GetDataType(char[] value) {
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
