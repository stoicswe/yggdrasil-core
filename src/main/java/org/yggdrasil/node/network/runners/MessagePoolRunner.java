package org.yggdrasil.node.network.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yggdrasil.node.network.messages.MessagePool;
import org.yggdrasil.node.network.messages.Messenger;

/**
 * The message pool runner will check the message pool periodically
 * in order to see if a message that was sent has "expired" meaning
 * that it was sent but without an acknowledgement. This is to ensure
 * data stability.
 *
 * @since 0.0.15
 * @author nathanielbunch
 */
public class MessagePoolRunner implements Runnable {

    Logger logger = LoggerFactory.getLogger(MessagePoolRunner.class);

    private Messenger messenger;
    private MessagePool messagePool;

    public MessagePoolRunner(Messenger messenger, MessagePool messagePool) {
        this.messenger = messenger;
        this.messagePool = messagePool;
    }

    @Override
    public void run() {
        while(true) {
            this.messagePool.checkMessages();
            //Only check every 30 seconds.
            //Thread.sleep(30000);
        }
    }

}
