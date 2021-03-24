package org.yggdrasil.node.network.data;

import org.yggdrasil.core.ledger.chain.Block;
import org.yggdrasil.core.ledger.transaction.Transaction;
import org.openjdk.jol.info.GraphLayout;

import java.io.Serializable;

public class Message implements Serializable {

    private final MessageIdentifier identifier;
    private final String sender;
    private final long dataSize;
    private final Object data;

    private Message(Builder builder) {
        this.identifier = builder.messageIdentifier;
        this.sender = builder.sender;
        this.dataSize = builder.dataSize;
        this.data = builder.data;
    }

    public MessageIdentifier getIdentifier() {
        return identifier;
    }

    public String getSender() {
        return sender;
    }

    public long getDataSize() {
        return dataSize;
    }

    public Object getData() {
        return data;
    }

    public class Builder {

        private MessageIdentifier messageIdentifier;
        private String sender;
        private long dataSize;
        private Object data;

        private Builder(){}

        public Builder setMessageIdentifier(MessageIdentifier messageIdentifier) {
            this.messageIdentifier = messageIdentifier;
            return this;
        }

        public Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder setDataSize(long dataSize) {
            this.dataSize = dataSize;
            return this;
        }

        public Builder setData(Object data) {
            this.data = data;
            return this;
        }

        public Builder newIdentityMessage(String nodeIdentifier) {
            this.messageIdentifier = MessageIdentifier.IDENTIFY_MESSAGE;
            this.sender = nodeIdentifier;
            this.dataSize = 0L;
            this.data = null;
            return this;
        }

        public Builder newBlockMessage(String nodeIdentifier, Block block) {
            this.messageIdentifier = MessageIdentifier.BLOCK_MESSAGE;
            this.sender = nodeIdentifier;
            this.dataSize = GraphLayout.parseInstance(block).totalSize();
            this.data = block;
            return this;
        }

        public Builder newTransactionMessage(String nodeIdentifier, Transaction transaction) {
            this.messageIdentifier = MessageIdentifier.TRANSACTIONAL_MESSAGE;
            this.sender = nodeIdentifier;
            this.dataSize = GraphLayout.parseInstance(transaction).totalSize();
            this.data = transaction;
            return this;
        }

        public Message buid() {
            return new Message(this);
        }

    }
}
