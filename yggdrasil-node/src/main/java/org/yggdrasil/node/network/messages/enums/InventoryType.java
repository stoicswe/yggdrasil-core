package org.yggdrasil.node.network.messages.enums;

public enum InventoryType {

    ERROR(0, "ERR"),
    MSG_TX(1, "MSG_TX"),
    MSG_BLOCK(2, "MSG_BLOCK"),
    MSG_FILTERED_BLOCK(3, "MSG_FILTERED_BLOCK"),
    MSG_CMPCT_BLOCK(4, "MSG_CMPT_BLOCK"),
    // TODO: Learn about witness
    MSG_WITNESS_TX(0x40000001, "MSG_WITNESS_TX"),
    MSG_WITNESS_BLOCK(0x40000002, "MSG_WITNESS_BLOCK"),
    MSG_FILTERED_WITNESS_BLOCK(0x40000003, "MSG_FILTERED_WITNESS_BLOCK");

    private int value;
    private String label;

    private static final InventoryType[] values = new InventoryType[]{
            ERROR,
            MSG_TX,
            MSG_BLOCK,
            MSG_FILTERED_BLOCK,
            MSG_CMPCT_BLOCK,
            MSG_WITNESS_TX,
            MSG_WITNESS_BLOCK,
            MSG_FILTERED_WITNESS_BLOCK
    };

    InventoryType(int value, String label){
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean isEqual(InventoryType other) {
        return this.value == other.value;
    }

    public static InventoryType getByValue(int value) {
        for(InventoryType it : values) {
            if(it.value == value) {
                return it;
            }
        }
        return null;
    }

    public static InventoryType getByLabel(String label) {
        for(InventoryType it : values) {
            if(it.label.contentEquals(label)) {
                return it;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
