package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.LoadWorld;
import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class ClientNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ClientNetwork.class);
    private final Messenger messenger = new Messenger();
    private final ConnectionToServer connection;

    public ClientNetwork(World world, String host, Integer tcpPort, Player player) throws UnknownHostException {
        super(world);
        this.connection = new ConnectionToServer(InetAddress.getByName(host), tcpPort, messenger, player);
    }

    @Override
    public void initialize() throws IOException, ClassNotFoundException {
        connection.initialize();
        LoadWorld worldData = connection.getWorldData();

        worldData.getEntities().forEach(entity -> world.addEntity(entity));
        world.mapTerrain();

        logger.info("Loaded {} entities", worldData.getEntities().size());
    }

    @Override
    public void update(long tick) {
        LinkedList<Message> messages = messenger.readInbox();

        // TODO: temporary generic example of handling messages
        messages.forEach(message -> logger.debug(
            "Client received {}: {}",
            message.getClass().getSimpleName(),
            message.toString()
        ));
    }

    @Override
    public void propagate(long tick) {
        Thread.yield();

        // TODO: temporary generic example of sending a message
        if (tick % 1000 == 0) {
            LinkedList<Message> messages = new LinkedList<>();

//            messages.add(new Ping(tick, id, Protocol.TCP));
//            messages.add(new Ping(tick, id, Protocol.UDP));

            messenger.writeOutbox(messages);
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        connection.close();
    }

}
