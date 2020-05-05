package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.apache.logging.log4j.core.net.Protocol.TCP;

public class Sender extends Thread {

    private static int counter = 0;

    private final Logger logger = LogManager.getLogger(Sender.class);
    private final ObjectOutputStream output;
    private final Messenger messenger;
    private final Connection connection;

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
                    log(message);
                }
            } catch (IOException e) {
                logger.error("Failed to send message", e);
            }
            Thread.yield();
        }
    }

    private void log(Message message) {
        Level level = message.getChannel().equals(TCP) ? Level.DEBUG : Level.TRACE;
        logger.log(level, "Sent {} to {}", message, connection);
    }

}
