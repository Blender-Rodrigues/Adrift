package ee.taltech.iti0200.network;

import ee.taltech.iti0200.domain.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.UUID;

public class ClientNetwork extends Network {

    private ClientSender sender;
    private ClientReceiver receiver;
    private Logger logger;

    public ClientNetwork(World world) throws SocketException, UnknownHostException {
        super(world);
        logger = LogManager.getLogger(ClientNetwork.class);
        UUID clientId = UUID.randomUUID();
        sender = new ClientSender(messenger, new ClientSocket(HOST, UPSTREAM_PORT, clientId, "Sender"));
        receiver = new ClientReceiver(messenger, new ClientSocket(HOST, DOWNSTREAM_PORT, clientId, "Receiver"));
    }

    @Override
    public void initialize() throws IOException {
        sender.initialize();
        receiver.initialize();
    }

    @Override
    public void update(long tick) {
        Thread.yield();
        LinkedList<String> messages = messenger.readInbox();

        logger.info("Received {} messages from server", messages.size());
        messages.forEach(logger::debug);
    }

    @Override
    public void propagate(long tick) {
        Thread.yield();
        LinkedList<String> messages = new LinkedList<>();

        messages.add("Client side tick:" + tick);

        messenger.writeOutbox(messages);
    }

}
