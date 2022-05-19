package org.yggdrasil.node.network.messages.payloads;

import org.yggdrasil.node.network.messages.MessagePayload;

public class TransactionWitness implements MessagePayload {
    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }
}
