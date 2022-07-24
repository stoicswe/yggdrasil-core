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
    @NotNull
    private final byte[] requestChecksum;

    private BlockHeaderResponsePayload(Builder builder) {
        this.headerCount = builder.headerCount;
        this.headers = builder.headers;
        this.requestChecksum = builder.requestChecksum;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    public BlockHeaderPayload[] getHeaders() {
        return headers;
    }

    public byte[] requestChecksum() {
        return requestChecksum;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(headerCount));
        for(BlockHeaderPayload hp : headers) {
            messageBytes = DataUtil.appendBytes(messageBytes, hp.getDataBytes());
        }
        messageBytes = DataUtil.appendBytes(messageBytes, requestChecksum);
        return messageBytes;
    }

    public static class Builder {

        private int headerCount;
        private BlockHeaderPayload[] headers;
        private byte[] requestChecksum;

        private Builder(){}

        public static Builder builder() {
            return new Builder();
        }

        public Builder setHeaders(BlockHeaderPayload[] headers) {
            this.headerCount = headers.length;
            this.headers = headers;
            return this;
        }

        public Builder setRequestChecksum(byte[] requestChecksum) {
            this.requestChecksum = requestChecksum;
            return this;
        }

        public BlockHeaderResponsePayload build() {
            return new BlockHeaderResponsePayload(this);
        }

    }
}
