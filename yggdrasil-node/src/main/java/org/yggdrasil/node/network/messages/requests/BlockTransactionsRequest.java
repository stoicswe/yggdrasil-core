package org.yggdrasil.node.network.messages.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.core.serialization.HashSerializer;
import org.yggdrasil.node.network.messages.MessagePayload;
import org.yggdrasil.node.network.messages.util.DataUtil;

import javax.validation.constraints.NotNull;

@JsonInclude
public class BlockTransactionsRequest implements MessagePayload {

    @NotNull
    @JsonSerialize(using = HashSerializer.class)
    private byte[] header;
    @NotNull
    private int indexesCount;
    @NotNull
    private int[] indexes;

    private BlockTransactionsRequest(Builder builder) {
        this.header = builder.header;
        this.indexesCount = builder.indexesCount;
        this.indexes = builder.indexes;
    }

    public byte[] getHeader() {
        return header;
    }

    public int getIndexesCount() {
        return indexesCount;
    }

    public int[] getIndexes() {
        return indexes;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = DataUtil.appendBytes(messageBytes, header);
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(indexesCount));
        messageBytes = DataUtil.appendBytes(messageBytes, SerializationUtils.serialize(indexes));
        return messageBytes;
    }

    public static class Builder {

        private byte[] header;
        private int indexesCount;
        private int[] indexes;

        public Builder builder() {
            return new Builder();
        }

        public Builder setHeader(byte[] header) {
            this.header = header;
            return this;
        }

        public Builder setIndexes(int[] indexes) {
            this.indexes = indexes;
            this.indexesCount = indexes.length;
            return this;
        }

        public BlockTransactionsRequest build() {
            return new BlockTransactionsRequest(this);
        }

    }
}
