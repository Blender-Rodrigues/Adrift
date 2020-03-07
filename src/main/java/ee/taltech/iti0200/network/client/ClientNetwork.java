package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Ping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.UUID;

public class ClientNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ClientNetwork.class);
    private final Messenger messenger = new Messenger();
    private final UUID id = UUID.randomUUID();
    private final ConnectionToServer connection;

    public ClientNetwork(World world, String host, Integer tcpPort) throws UnknownHostException {
        super(world);
        connection = new ConnectionToServer(InetAddress.getByName(host), tcpPort, messenger, id);
    }

    @Override
    public void initialize() throws IOException, ClassNotFoundException {
        connection.initialize();
    }

    @Override
    public void update(long tick) {
        LinkedList<Message> messages = messenger.readInbox();
        messages.forEach(message -> logger.debug(
            "Client received {}: {}",
            message.getClass().getSimpleName(),
            message.toString()
        ));
    }

    @Override
    public void propagate(long tick) {
        Thread.yield();
        if (tick % 1000 == 0) {
            LinkedList<Message> messages = new LinkedList<>();

            messages.add(new Ping(tick, id, Protocol.TCP));
            messages.add(new Ping(tick, id, Protocol.UDP));

            messenger.writeOutbox(messages);
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        connection.close();
    }

}
