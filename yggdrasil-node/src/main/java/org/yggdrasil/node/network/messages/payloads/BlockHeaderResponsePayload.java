package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

/**
 * The Header message is a response to the getData message that is requesting
 * either block or transaction headers. The internal headerPayload property
 * contains the actual headers (block or transaction).
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
@JsonInclude
public class BlockHeaderResponsePayload implements MessagePayload {

    @NotNull
    private final int headerCount;
    @NotNull
    private final BlockHeaderPayload[] headers;

    private BlockHeaderResponsePayload(Builder builder) {
        this.headerCount = builder.headerCount;
        this.headers = builder.headers;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    public BlockHeaderPayload[] getHeaders() {
        return headers;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(headerCount));
        for(BlockHeaderPayload hp : headers) {
            messageBytes = DataUtil.appendBytes(messageBytes, hp.getDataBytes());
        }
        return messageBytes;
    }

    public static class Builder {

        private int headerCount;
        private char[] headerType;
        private BlockHeaderPayload[] headers;
        private byte[] headerHash;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setHeaderCount(int headerCount) {
            this.headerCount = headerCount;
            return this;
        }

        public Builder setHeaders(BlockHeaderPayload[] headers) {
            this.headers = headers;
            return this;
        }

        public BlockHeaderResponsePayload build() {
            return new BlockHeaderResponsePayload(this);
        }

    }
}
