package org.yggdrasil.node.network.messages.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yggdrasil.core.exception.InvalidMessageException;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.Message;

/**
 * The Message validator will verify that the contents of the message was not messed
 * with or corrupted before allowing consumption.
 *
 * @since 0.0.11
 * @author nathanielbunch
 */
@Component
public class MessageValidator {

    @Autowired
    private CryptoHasher hasher;

    public void isValidMessage(Message message) {
        if(message != null && message.getPayload() != null) {

        } else {
            throw new InvalidMessageException("");
        }
    }

}
