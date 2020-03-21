package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Sender extends Thread {

    private static int counter = 0;

    private final Logger logger = LogManager.getLogger(Sender.class);
    private final ObjectOutputStream output;
    protected final Messenger messenger;
    protected final Connection connection;

    public Sender(String name, ObjectOutputStream output, Messenger messenger, Connection connection) {
        setName(name + " Sender " + counter++);
        this.messenger = messenger;
        this.output = output;
        this.connection = connection;
    }

    public void run() {
        while (messenger.isAlive() && connection.isOpen()) {
            try {
                Message message = messenger.readOutbox();
                if (message != null) {
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
