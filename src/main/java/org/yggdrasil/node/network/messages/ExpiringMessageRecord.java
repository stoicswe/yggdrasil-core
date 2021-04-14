package org.yggdrasil.node.network.messages;

import org.apache.commons.lang3.tuple.Pair;

public class ExpiringMessageRecord<ZonedDateTime, Message> extends Pair<ZonedDateTime, Message> {

    ZonedDateTime timestamp;
    Message message;

    public ExpiringMessageRecord(ZonedDateTime currentTimestamp, Message message) {
        this.timestamp = currentTimestamp;
        this.message = message;
    }

    @Override
    public ZonedDateTime getLeft() {
        return null;
    }

    @Override
    public Message getRight() {
        return null;
    }

    @Override
    public Message setValue(Message value) {
        return null;
    }
}
