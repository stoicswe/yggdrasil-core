package org.yggdrasil.node.network.messages;

import org.apache.commons.lang3.tuple.Triple;

import java.time.ZonedDateTime;

public class ExpiringMessageRecord extends Triple<ZonedDateTime, String, Message> {

    ZonedDateTime timestamp;
    String destination;
    Message message;

    public ExpiringMessageRecord(ZonedDateTime currentTimestamp, String destination, Message message) {
        this.timestamp = currentTimestamp;
        this.destination = destination;
        this.message = message;
    }

    @Override
    public ZonedDateTime getLeft() {
        return this.timestamp;
    }

    @Override
    public String getMiddle() {
        return this.destination;
    }

    @Override
    public Message getRight() {
        return this.message;
    }
}
