package ee.taltech.iti0200.network;

import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.function.Consumer;

public class TcpListener extends Thread {

    private static final int ERROR_LIMIT = 5;

    private final Logger logger = LogManager.getLogger(TcpListener.class);
    private final Messenger messenger;
    private final ObjectInputStream input;
    private final Map<Class<? extends Message>, Consumer<Message>> handlers;

    private int errors = 0;

    public TcpListener(
        ObjectInputStream input,
        Messenger messenger,
        Map<Class<? extends Message>, Consumer<Message>> handlers
    ) {
        this.messenger = messenger;
        this.input = input;
        this.handlers = handlers;
    }

    public void run() {
        while (messenger.isAlive()) {
            Thread.yield();
            Message response;

            try {
                response = (Message) input.readObject();
            } catch (IOException | ClassNotFoundException e) {
                errors++;
                if (errors >= ERROR_LIMIT) {
                    logger.error("Failed to read " + errors + " subsequent messages. Closing thread. " + e.getMessage(), e);
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
