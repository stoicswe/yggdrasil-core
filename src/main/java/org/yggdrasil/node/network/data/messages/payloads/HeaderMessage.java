package org.yggdrasil.node.network.data.messages.payloads;

import org.yggdrasil.node.network.data.messages.MessagePayload;
import org.yggdrasil.node.network.data.messages.enums.HeaderType;

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
            this.headerType = headerType.getValue();
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
