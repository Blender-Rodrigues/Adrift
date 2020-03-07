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

    private ConnectionToClient connection;

    public VerifiedSender(String name, ConnectionToClient connection, ObjectOutputStream output, Messenger messenger) {
        super(name, output, messenger, connection);
        this.connection = connection;
    }

    /**
     * Since the client side UDP listener port has not yet talked to the server we might get blocked by the firewall
     * and thus we wait for a registration packet from that port before starting to send server UDP messages there.
     */
    public void run() {
        logger.info("Waiting for client UDP connection.");
        while (messenger.isAlive() && connection.isOpen()) {
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

        connection.finalized();
        logger.info("Client {} registration completed.", connection.getId());
        super.run();
    }

}
