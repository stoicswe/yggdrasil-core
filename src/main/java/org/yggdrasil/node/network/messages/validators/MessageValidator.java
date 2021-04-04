package org.yggdrasil.node.network.messages.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.yggdrasil.node.network.exceptions.InvalidMessageException;
import org.yggdrasil.core.utils.CryptoHasher;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePayload;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

/**
 * The Message validator will verify that the contents of the message was not messed
 * with or corrupted before allowing consumption.
 *
 * @since 0.0.11
 * @author nathanielbunch
 */
@Component
@Validated
public class MessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(MessageValidator.class);

    public void isValidMessage(@Valid Message message) throws NoSuchAlgorithmException {
        logger.info("Checking message checksum.");
        this.validateChecksum(message.getPayload(), message.getChecksum());
    }

    private void validateChecksum(MessagePayload payload, byte[] messageChecksum) throws NoSuchAlgorithmException, InvalidMessageException {
        byte[] payloadHash = CryptoHasher.hash(payload);
        try {
            if(messageChecksum.length == payloadHash.length) {
                for (int i = 0; i < messageChecksum.length; i++) {
                    if (!(messageChecksum[i] == payloadHash[i])) {
                        throw new InvalidMessageException("Checksum did not match payload hash.");
                    }
                }
                return;
            }
            throw new InvalidMessageException("Checksum did not match payload hash.");
        } catch (Exception e) {
            logger.error("Message received was invalid.");
            throw new InvalidMessageException("Checksum did not match payload hash.");
        }
    }

}
