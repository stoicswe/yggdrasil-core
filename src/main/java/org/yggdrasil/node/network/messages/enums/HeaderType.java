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
    private static final HeaderType[] values = new HeaderType[] {BLOCK_HEADER, TXN_HEADER};

    HeaderType(String value) {
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

    public static HeaderType getByValue(char[] messageValue) {
        for(HeaderType rt : values) {
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
