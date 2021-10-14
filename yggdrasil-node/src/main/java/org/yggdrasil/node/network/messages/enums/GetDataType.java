package org.yggdrasil.node.network.messages.enums;

/**
 * The GetDataType is an identifying enum for getData requests.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public enum GetDataType {

    BLOCKCHAIN("BLKC"),
    BLOCK("BLKS"),
    TRANSACTION("TXNS"),
    MEMPOOL("MMPL");

    private String value;
    private static final GetDataType[] values = new GetDataType[]{BLOCKCHAIN, BLOCK, TRANSACTION, MEMPOOL};

    GetDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public char[] getMessageValue() {
        return value.toCharArray();
    }

    public boolean isEqualToLiteral(char[] literal){
        return this.value.contentEquals(String.valueOf(literal));
    }

    public static GetDataType getByValue(char[] messageValue) {
        for(GetDataType rt : values) {
            if(rt.isEqualToLiteral(messageValue)){
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
