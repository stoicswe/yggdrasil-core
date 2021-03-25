package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.enums.HeaderType;

/**
 * The Header message is a response to the getData message that is requesting
 * either block or transaction headers. The internal headerPayload property
 * contains the actual headers (block or transaction).
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public class HeaderMessage implements MessagePayload {

    private final int headerCount;
    private final char[] headerType;
    private final HeaderPayload[] headers;

    private HeaderMessage(Builder builder) {
        this.headerCount = builder.headerCount;
        this.headerType = builder.headerType;
        this.headers = builder.headers;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    public char[] getHeaderType() {
        return headerType;
    }

    public HeaderPayload[] getHeaders() {
        return headers;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(headerCount));
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(headerType));
        for(HeaderPayload hp : headers) {
            messageBytes = appendBytes(messageBytes, hp.getDataBytes());
        }
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private int headerCount;
        private char[] headerType;
        private HeaderPayload[] headers;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setHeaderCount(int headerCount) {
            this.headerCount = headerCount;
            return this;
        }

        public Builder setHeaderType(HeaderType headerType) {
            this.headerType = headerType.getMessageValue();
            return this;
        }

        public Builder setHeaders(HeaderPayload[] headers) {
            this.headers = headers;
            return this;
        }

        public HeaderMessage build() {
            return new HeaderMessage(this);
        }

    }
}
