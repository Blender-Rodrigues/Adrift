package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.LoadWorld;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ee.taltech.iti0200.application.Game.eventBus;
import static java.net.InetAddress.getByName;

public class ClientNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ClientNetwork.class);
    private final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> tcpOutbox = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> udpOutbox = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final Messenger messenger = new ClientMessenger(inbox, tcpOutbox,udpOutbox, alive);
    private final ConnectionToServer connection;
    private Player player;

    public ClientNetwork(World world, String host, Integer tcpPort, Player player) throws UnknownHostException {
        super(world);
        this.player = player;
        this.connection = new ConnectionToServer(getByName(host), tcpPort, inbox, tcpOutbox, udpOutbox, alive, player);
    }

    @Override
    public void initialize() throws IOException, ClassNotFoundException {
        connection.initialize();
        LoadWorld worldData = connection.getWorldData();

        worldData.getEntities().forEach(world::addEntity);
        world.mapTerrain();

        logger.info("Loaded {} entities", worldData.getEntities().size());
    }

    @Override
    protected Messenger getMessenger() {
        return messenger;
    }

    @Override
    public void propagate(long tick) {
        Thread.yield();

        List<Message> events = eventBus.propagateAll()
            .stream()
            .filter(event -> {
                Receiver receiver = event.getReceiver();
                if (Receiver.EVERYONE.equals(receiver)) {
                    event.setReceiver(Receiver.SERVER);
                    return true;
                }
                return Receiver.SERVER.equals(event.getReceiver());
            })
            .collect(Collectors.toList());

        // TODO: send only if the player has moved
        events.add(new UpdateVector(player, Receiver.SERVER));

        messenger.writeOutbox(events);
    }

    @Override
    public void terminate() {
        super.terminate();
        connection.close();
    }

}
