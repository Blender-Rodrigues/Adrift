package ee.taltech.iti0200.network.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.LoadWorld;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClientNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ClientNetwork.class);
    private final Messenger messenger;
    private final ConnectionToServer connection;
    private final Player player;

    @Inject
    public ClientNetwork(
        World world,
        EventBus eventBus,
        @LocalPlayer Player player,
        ConnectionToServer connection,
        Messenger messenger
    ) {
        super(world, eventBus);
        this.player = player;
        this.messenger = messenger;
        this.connection = connection;
    }

    @Override
    public void initialize() throws IOException, ClassNotFoundException {
        connection.initialize();
        LoadWorld worldData = connection.getWorldData();

        worldData.getEntities().forEach(world::addEntity);
        world.mapTerrain();
        player.setPosition(worldData.getSpawn());

        logger.info(
            "Loaded {} entities, set player coordinates to {}",
            worldData.getEntities().size(),
            worldData.getSpawn()
        );
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
        events.add(new UpdateVector(player, tick, Receiver.SERVER));

        messenger.writeOutbox(events);
    }

    @Override
    public void terminate() {
        super.terminate();
        connection.close();
    }

}
