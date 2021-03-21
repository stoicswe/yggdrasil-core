package org.yggdrasil.node.network.data;

public enum MessageIdentifier {

    IDENTIFY_MESSAGE("identification", 1),
    BLOCK_MESSAGE("block", 2),
    TRANSACTIONAL_MESSAGE("transaction", 3);

    private int value;
    private String name;

    MessageIdentifier(String name, int value){
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public boolean equals(MessageIdentifier other){
        if(this.value == other.value) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
