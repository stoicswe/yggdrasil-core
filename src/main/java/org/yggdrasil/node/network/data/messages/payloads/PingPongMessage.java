package org.yggdrasil.node.network.data.messages.payloads;

import org.yggdrasil.node.network.data.messages.MessagePayload;

/**
 * The Ping Pong Message is used to communicate back and forth to see if a conneciton
 * is alive between nodes.
 *
 * @since 0.0.1
 * @author nathanielbunch
 */
public class PingPongMessage implements MessagePayload {

    private final int nonce;

    private PingPongMessage(Builder builder) {
        this.nonce = builder.nonce;
    }

    public int getNonce() {
        return nonce;
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
