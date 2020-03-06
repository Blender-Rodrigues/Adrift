package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Sender extends Thread {

    private final Logger logger = LogManager.getLogger(Sender.class);
    private final ObjectOutputStream output;
    protected final Messenger messenger;

    public Sender(ObjectOutputStream output, Messenger messenger) {
        this.messenger = messenger;
        this.output = output;
    }

    public void run() {
        while (messenger.isAlive()) {
            try {
                Message message = messenger.readOutbox();
                if (message != null) {
                    logger.info("Sending {} message", message.getClass().getSimpleName());
                    output.writeObject(message);
                    output.flush();
                }
            } catch (IOException e) {
                logger.error("Failed to send message", e);
            }
            Thread.yield();
        }
    }

}
