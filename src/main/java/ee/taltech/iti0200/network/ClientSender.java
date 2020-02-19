package ee.taltech.iti0200.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ClientSender extends Thread {

    private final Messenger messenger;
    private ClientSocket socket;
    private Logger logger;

    public ClientSender(
        Messenger messenger,
        ClientSocket socket
    ) {
        this.messenger = messenger;
        this.socket = socket;
        logger = LogManager.getLogger(ClientSender.class);
    }

    public void initialize() throws IOException {
        socket.confirmConnection(0);
        super.start();
    }

    @Override
    public void run() {
        while (messenger.isAlive()) {
            try {
                String message = messenger.readOutbox();

                socket.send(message.getBytes());

                logger.info("Sent to server: " + message);
            } catch (IOException e) {
                logger.error("Failed to send message to server", e);
            }
            Thread.yield();
        }
    }

}
