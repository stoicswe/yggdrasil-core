package org.yggdrasil.node.network.messages.enums;

/**
 * The RequestType is an identifying enum for
 * message headers and helps nodes handle data
 * properly.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public enum CommandType {

    // Request Message Types
    REQUEST_BLOCK_HEADER("GBLKHDR", "GET_BLOCK_HEADER"),
    REQUEST_BLOCK("GBLK", "GET_BLOCK"),
    REQUEST_BLOCK_TXNS("GBLKTXNS", "GET_BLOCK_TXNS"),
    REQUEST_MEMPOOL_TXNS("GMEMPOOLTXNS", "GET_MEMPOOL_TXNS"),
    // Instead of sending actual payload data
    // send an empty payload with this enum for
    // requesting all latest mempool items from a
    // specific node.
    REQUEST_MEMPOOL_LATEST("GMEMPOOLLATEST", "GET_MEMPOOL_LATEST"),
    REQUEST_ADDRESS("GADDRESS", "GET_ADDRESS"),

    // Response (Payload) Types
    ACKNOWLEDGE_PAYLOAD("VRKPYLD", "ACKNOWLEDGE_PAYLOAD"),
    ADDRESS_PAYLOAD("ADDRPYLD", "ADDRESS_PAYLOAD"),
    BLOCK_HEADER_PAYLOAD("BLKHDRPYLD", "BLOCK_HEADER_PAYLOAD"),
    BLOCK_PAYLOAD("BLKPYLD", "BLOCK_PAYLOAD"),
    BLOCK_TXN_PAYLOAD("BLKTXNPYLD", "BLOCK_TXN_PAYLOAD"),
    INVENTORY_PAYLOAD("INVTPYLD", "INVENTORY_PAYLOAD"),
    PREFILLED_TXN_PAYLOAD("PFTXNPYLD", ""),
    TRANSACTION_PAYLOAD("TXNPYLD", "TRANSACTION_PAYLOAD"),
    TXN_WITNESS_PAYLOAD("TXNWTNSSPYLD", "TXN_WITNESS_PAYLOAD"),

    // Error Types
    NOT_FOUND_PAYLOAD("404PYLD", "NOT_FOUND_PAYLOAD"),
    REJECT_PAYLOAD("RJCTPYLD", "REJECT_PAYLOAD"),

    // Utility Types
    PING("UTLPING", "PING"),
    PONG("UTLPONG", "PONG"),
    HANDSHAKE_OFFR("UTLOFFHNDSHK", "HANDSHAKE_OFFR"),
    HANDSHAKE_RESP("UTLRTNHNDSHK", "HANDSHAKE_RESP");

    private static final CommandType[] values = new CommandType[]{
            // Request types
            REQUEST_BLOCK_HEADER,REQUEST_BLOCK,REQUEST_BLOCK_TXNS,REQUEST_MEMPOOL_TXNS,
            REQUEST_MEMPOOL_LATEST, REQUEST_ADDRESS,
            // Response (Payload) Types
            ACKNOWLEDGE_PAYLOAD, ADDRESS_PAYLOAD, BLOCK_HEADER_PAYLOAD, BLOCK_PAYLOAD,
            BLOCK_TXN_PAYLOAD, PREFILLED_TXN_PAYLOAD, TRANSACTION_PAYLOAD, TXN_WITNESS_PAYLOAD,
            INVENTORY_PAYLOAD,
            // Error Types
            NOT_FOUND_PAYLOAD, REJECT_PAYLOAD,
            // Utility Types
            PING, PONG, HANDSHAKE_OFFR, HANDSHAKE_RESP
    };

    private char[] value;
    private String label;

    CommandType(String value, String label) {
        this.value = value.toCharArray();
        this.label = label;
    }

    public char[] getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    public static CommandType getByValue(char[] value) {
        for(CommandType rt : values) {
            if(new String(rt.getValue()).contentEquals(new String(value))){
                return rt;
            }
        }
        return null;
    }

    public static CommandType getByLabel(String label) {
        for(CommandType rt : values) {
            if(rt.label.contentEquals(label)){
                return rt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
