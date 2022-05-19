package org.yggdrasil.node.network.messages.enums;

public enum ServicesType {

    NODE_NETWORK(1, "FULL NODE"),
    NODE_GETUTXO(2, "GET UTXO"),
    NODE_BLOOM(3, "BLOOM"),
    NODE_WITNESS(4, "WITNESS"),
    NODE_COMPACT_FILTERS(5, "COMPACT"),
    NODE_NETWORK_LIMITED(6, "LIMITED");

    private final int value;
    private final String label;

    // TODO: As functionality is written, add more of these service types
    private static final ServicesType[] values = new ServicesType[]{
            NODE_NETWORK,
            //NODE_GETUTXO,
            //NODE_BLOOM,
            //NODE_WITNESS,
            //NODE_COMPACT_FILTERS,
            //NODE_NETWORK_LIMITED
    };

    ServicesType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    public static ServicesType getByValue(int value) {
        for(ServicesType s : values) {
            if (s.value == value) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
