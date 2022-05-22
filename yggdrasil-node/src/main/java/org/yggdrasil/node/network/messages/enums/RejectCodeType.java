package org.yggdrasil.node.network.messages.enums;

public enum RejectCodeType {

    REJECT_MALFORMED(0x01, "MALFORMED"),
    REJECT_INVALID(0x02, "INVALID"),
    REJECT_OBSOLETE(0x03, "OBSOLETE"),
    REJECT_DUPLICATE(0x04, "DUPLICATE"),
    REJECT_NONSTANDARD(0x05, "NONSTANDARD"),
    REJECT_DUST(0x06, "DUST"),
    REJECT_INSUFFICIENTFEE(0x07, "INSUFFICIENTFEE"),
    REJECT_CHECKPOINT(0x08, "CHECKPOINT");

    private int value;
    private String label;

    private static RejectCodeType[] values = new RejectCodeType[]{
            REJECT_MALFORMED,
            REJECT_INVALID,
            REJECT_OBSOLETE,
            REJECT_DUPLICATE,
            REJECT_NONSTANDARD,
            REJECT_DUST,
            REJECT_INSUFFICIENTFEE,
            REJECT_CHECKPOINT
    };

    private RejectCodeType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static RejectCodeType getByValue(int value) {
        for(RejectCodeType c : values) {
            if(c.value == value){
                return c;
            }
        }
        return null;
    }

    public static RejectCodeType getByLabel(String label) {
        for(RejectCodeType c : values) {
            if(c.label.contentEquals(label)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }

}
