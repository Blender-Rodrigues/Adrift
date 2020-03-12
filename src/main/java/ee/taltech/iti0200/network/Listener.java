package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;

public class Listener extends Thread {

    private static final int ERROR_LIMIT = 5;
    private static int counter = 0;

    private final Logger logger = LogManager.getLogger(Listener.class);
    private final Messenger messenger;
    private final ObjectInputStream input;
    private final Connection connection;
    private final Map<Class<? extends Message>, Consumer<Message>> handlers;

    private int errors = 0;

    public Listener(String name, ObjectInputStream input, Messenger messenger, Connection connection) {
        this(name, input, messenger, connection, new HashMap<>());
    }

    public Listener(
        String name,
        ObjectInputStream input,
        Messenger messenger,
        Connection connection,
        Map<Class<? extends Message>, Consumer<Message>> handlers
    ) {
        setName(name + " Listener " + counter++);
        this.input = input;
        this.messenger = messenger;
        this.connection = connection;
        this.handlers = handlers;
    }

    /**
     * Tolerate up to 5 subsequent failed incoming message reads before closing down the connection.
     */
    public void run() {
        while (messenger.isAlive() && connection.isOpen()) {
            Thread.yield();
            Message response;

            try {
                response = (Message) input.readObject();
            } catch (EOFException e) {
                logger.warn("Connection closed");
                logger.trace(e.getMessage(), e);
                connection.close();
                return;
            } catch (IOException | ClassNotFoundException e) {
                if (!connection.isOpen()) {
                    logger.warn("Connection closed");
                    logger.trace(e.getMessage(), e);
                    return;
                }

                errors++;
                if (errors >= ERROR_LIMIT) {
                    logger.error(
                        format("Failed to read %s subsequent messages. Closing connection. %s", errors, e.getMessage()),
                        e
                    );
                    connection.close();
                    return;
                } else {
                    logger.error("Failed to read message " + e.getMessage(), e);
                }
                continue;
            }

            errors = 0;
            Class<? extends Message> type = response.getClass();

            if (handlers.containsKey(type)) {
                handlers.get(type).accept(response);
                continue;
            }

            messenger.writeInbox(response);
            logger.error("Message {} received", type.getSimpleName());
        }
    }

}
