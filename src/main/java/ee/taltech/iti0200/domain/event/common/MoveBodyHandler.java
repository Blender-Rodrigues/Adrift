package ee.taltech.iti0200.domain.event.common;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.GameId;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class MoveBodyHandler implements Subscriber<UpdateVector> {

    private final Logger logger = LogManager.getLogger(MoveBodyHandler.class);

    private World world;
    private UUID local;

    @Inject
    public MoveBodyHandler(World world, @GameId UUID local) {
        this.world = world;
        this.local = local;
    }

    /**
     * Local id is for the player itself on the client side
     * TODO: should fix location if diff too large?
     */
    @Override
    public void handle(UpdateVector event) {
        if (local.equals(event.getId())) {
            return;
        }

        Entity entity = world.getEntity(event.getId());
        if (entity == null) {
            logger.trace("Vector update for a non-existing entity {} at {}", event.getId(), event.getPosition());
            return;
        }

        entity.setPosition(event.getPosition());
        entity.setSpeed(event.getSpeed());

        if (entity instanceof Living) {
            logger.trace("Moved {}", entity);
        }
    }

}
