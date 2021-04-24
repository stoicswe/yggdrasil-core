package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.exceptions.NodeDisconnectException;
import org.yggdrasil.node.network.messages.ExpiringMessageRecord;
import org.yggdrasil.node.network.messages.Message;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.Messenger;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.TimerTask;

/**
 * The message pool runner will check the message pool periodically
 * in order to see if a message that was sent has "expired" meaning
 * that it was sent but without an acknowledgement. This is to ensure
 * data stability.
 *
 * @since 0.0.15
 * @author nathanielbunch
 */
public class MessagePoolRunner extends TimerTask {

    Logger logger = LoggerFactory.getLogger(MessagePoolRunner.class);

    private Messenger messenger;
    private MessagePool messagePool;

    public MessagePoolRunner(Messenger messenger, MessagePool messagePool) {
        this.messenger = messenger;
        this.messagePool = messagePool;
    }

    @Override
    public void run() {
        logger.info("Checking for any expired messages.");
        List<ExpiringMessageRecord> expiringMessages = this.messagePool.checkMessages();
        for(ExpiringMessageRecord exmr : expiringMessages) {
            try {
                this.messenger.sendTargetMessage((Message) exmr.getRight(), (String) exmr.getMiddle());
            } catch (NodeDisconnectException | IOException | NoSuchAlgorithmException e) {
                logger.debug("Exception received when trying to resubmit message, removing expired message from message pool: [{}]", exmr.getRight().toString());
                this.messagePool.removeMessage(((Message) exmr.getRight()).getChecksum());
            }
        }
    }

}
