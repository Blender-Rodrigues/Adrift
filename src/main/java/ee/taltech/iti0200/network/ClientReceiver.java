package ee.taltech.iti0200.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ClientReceiver extends Thread {

    private final Messenger messenger;
    private ClientSocket socket;
    private Logger logger;

    public ClientReceiver(
        Messenger messenger,
        ClientSocket socket
    ) {
        this.messenger = messenger;
        this.socket = socket;
        logger = LogManager.getLogger(ClientReceiver.class);
    }

    public void initialize() throws IOException {
        socket.confirmConnection(0);
        super.start();
    }

    @Override
    public void run() {
        while (messenger.isAlive()) {
            try {
                String message = socket.receive();

                messenger.writeInbox(message);

                logger.info("Received server message: " + message);
            } catch (IOException e) {
                logger.error("Failed to read server response", e);
            }
            Thread.yield();
        }
    }

}
