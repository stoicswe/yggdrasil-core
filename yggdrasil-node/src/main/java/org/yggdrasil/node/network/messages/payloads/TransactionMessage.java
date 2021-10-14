package org.yggdrasil.node.network.messages.payloads;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * The Transaction Message contains a list of transaction message data.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
public class TransactionMessage implements MessagePayload {

    @NotNull
    private final int txnCount;
    @NotNull
    private final TransactionPayload[] txns;

    private TransactionMessage(Builder builder) {
        this.txnCount = builder.txnCount;
        this.txns = builder.txns;
    }

    public int getTxnCount() {
        return txnCount;
    }

    public TransactionPayload[] getTxns() {
        return txns;
    }

    @Override
    public byte[] getDataBytes() {
        byte[] messageBytes = new byte[0];
        messageBytes = appendBytes(messageBytes, SerializationUtils.serialize(txnCount));
        for(TransactionPayload txnp : txns) {
            messageBytes = appendBytes(messageBytes, txnp.getDataBytes());
        }
        return messageBytes;
    }

    private static byte[] appendBytes(byte[] base, byte[] extension) {
        return ArrayUtils.addAll(base, extension);
    }

    public static class Builder {

        private int txnCount;
        private TransactionPayload[] txns;

        private Builder(){}

        public static Builder newBuilder(){
            return new Builder();
        }

        public Builder setTxnCount(int txnCount) {
            this.txnCount = txnCount;
            return this;
        }

        public Builder setTxns(TransactionPayload[] txns) {
            this.txns = txns;
            return this;
        }

        public TransactionMessage build() {
            return new TransactionMessage(this);
        }

    }

}
