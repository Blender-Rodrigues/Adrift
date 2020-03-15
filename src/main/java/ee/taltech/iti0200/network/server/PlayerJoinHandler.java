package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.CreatePlayer;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.message.LoadWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class PlayerJoinHandler implements Subscriber<CreatePlayer> {

    private final Logger logger = LogManager.getLogger(PlayerJoinHandler.class);

    private World world;
    private Messenger messenger;

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

        Player player = new Player(event.getPosition(), world);
        player.setId(event.getId());
        player.setXSpeed(event.getSpeed().x);
        player.setYSpeed(event.getSpeed().y);

        logger.info("Added new player {} to the world at {}", event.getId(), event.getPosition());

        world.addEntity(player);

        messenger.writeOutbox(asList(
            event,
            new LoadWorld(entities, event.getId())
        ));
    }

}
