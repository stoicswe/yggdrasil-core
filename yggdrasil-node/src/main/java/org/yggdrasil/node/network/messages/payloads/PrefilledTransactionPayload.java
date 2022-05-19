package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

public class PrefilledTransactionPayload implements MessagePayload {
    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }
}
