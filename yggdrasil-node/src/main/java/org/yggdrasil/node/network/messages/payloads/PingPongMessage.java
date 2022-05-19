package org.yggdrasil.node.network.messages.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.SerializationUtils;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.constraints.NotNull;

/**
 * The Ping Pong Message is used to communicate back and forth to see if a conneciton
 * is alive between nodes.
 *
 * @since 0.0.10
 * @author nathanielbunch
 */
@JsonInclude
public class PingPongMessage implements MessagePayload {

    @NotNull
    private final int nonce;

    private PingPongMessage(Builder builder) {
        this.nonce = builder.nonce;
    }

    public int getNonce() {
        return nonce;
    }

    @Override
    public byte[] getDataBytes() {
        return SerializationUtils.serialize(nonce);
    }

    public static class Builder {

        private int nonce;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setNonce(int nonce) {
            this.nonce = nonce;
            return this;
        }

        public PingPongMessage build() {
            return new PingPongMessage(this);
        }

    }
}
