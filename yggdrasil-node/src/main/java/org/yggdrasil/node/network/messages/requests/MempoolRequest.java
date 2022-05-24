package org.yggdrasil.node.network.messages.requests;

import org.yggdrasil.node.network.messages.MessagePayload;

public class MempoolRequest implements MessagePayload {



    @Override
    public byte[] getDataBytes() {
        return new byte[0];
    }
}
