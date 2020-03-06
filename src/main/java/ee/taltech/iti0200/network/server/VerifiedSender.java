package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Sender;
import ee.taltech.iti0200.network.message.UdpRegistrationRequest;
import ee.taltech.iti0200.network.message.UdpRegistrationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class VerifiedSender extends Sender {

    private final Logger logger = LogManager.getLogger(VerifiedSender.class);

    private ClientConnection connection;

    public VerifiedSender(ClientConnection connection, ObjectOutputStream output, Messenger messenger) {
        super(output, messenger);
        this.connection = connection;
    }

    public void run() {
        logger.info("Waiting for client UDP connection.");
        while (messenger.isAlive()) {
            Thread.yield();
            Object message;

            try {
                message = connection.getUdpInput().readObject();
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Failed to read message " + e.getMessage(), e);
                continue;
            }

            if (!(message instanceof UdpRegistrationRequest)) {
                logger.warn("Message {} received before registration", message.getClass().getSimpleName());
                continue;
            }

            try {
                connection.getTcpOutput().writeObject(new UdpRegistrationResponse());
            } catch (IOException e) {
                logger.error("Failed to respond to registration request " + e.getMessage(), e);
                continue;
            }

            break;
        }
        logger.info("Client UDP registration received, switching to sending mode.");
        super.run();
    }

}
