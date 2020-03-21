package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoveBodyHandler implements Subscriber<UpdateVector> {

    private final Logger logger = LogManager.getLogger(MoveBodyHandler.class);

    private World world;

    public MoveBodyHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(UpdateVector event) {
        Entity entity = world.getEntity(event.getId());
        if (entity == null) {
            logger.trace("Vector update for a non-existing entity {} at {}", event.getId(), event.getPosition());
            return;
        }

        entity.getBoundingBox().getCentre().set(event.getPosition());
        entity.setXSpeed(event.getSpeed().x);
        entity.setYSpeed(event.getSpeed().y);

        if (entity instanceof Living) {
            logger.trace("Moved {}", entity);
        }
    }

}
