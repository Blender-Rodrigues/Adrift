package ee.taltech.iti0200.network.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.CreatePlayer;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.message.LoadWorld;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class PlayerJoinHandler implements Subscriber<CreatePlayer> {

    private final Logger logger = LogManager.getLogger(PlayerJoinHandler.class);

    private World world;
    private Messenger messenger;

    @Inject
    public PlayerJoinHandler(World world, Messenger messenger) {
        this.world = world;
        this.messenger = messenger;
    }

    @Override
    public void handle(CreatePlayer event) {
        if (world.getEntity(event.getId()) != null) {
            logger.warn("Player {} is already present in world.", event.getId());
            return;
        }

        ArrayList<Entity> entities = new ArrayList<>(world.getEntities());

        Vector position = world.nextPlayerSpawnPoint();

        Player player = (Player) event.getEntity();
        player.getBoundingBox().getCentre().set(position);

        world.addEntity(player);

        logger.info("Added new {} to the world", player);

        messenger.writeOutbox(asList(
            new CreateEntity(player, new Receiver(event.getId()).exclude()),
            new LoadWorld(entities, position, new Receiver(event.getId()))
        ));
    }

}
