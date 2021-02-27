package org.nathanielbunch.ssblockchain.node.network.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MessageSendHandler implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MessageSendHandler.class);

    private Message message;
    private Socket socket;

    public MessageSendHandler(Message message, Socket destination) {
        this.message = message;
        this.socket = destination;
    }

    @Override
    public void run(){
        try(OutputStream outputStream = socket.getOutputStream()){
            try(ObjectOutputStream objOut = new ObjectOutputStream(outputStream)){
                objOut.writeObject(message);
            }
        } catch (IOException e) {
            logger.warn("Unable to finish sending message: {}", e.getMessage());
        }
    }

}
