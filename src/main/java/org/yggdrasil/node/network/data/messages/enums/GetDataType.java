package org.yggdrasil.node.network.data.messages.enums;

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
