package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.RemoveEntity;
import ee.taltech.iti0200.domain.event.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityRemoveHandler implements Subscriber<RemoveEntity> {

    private final Logger logger = LogManager.getLogger(EntityRemoveHandler.class);

    private World world;

    public EntityRemoveHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(RemoveEntity event) {
        Entity entity = event.getEntity();
        world.removeEntity(entity);

        if (entity instanceof Projectile) {
            logger.debug("Removed {} with ID {} from the world", entity, event.getId());
            event.stop();
        } else {
            logger.info("Removed {} with ID {} from the world", entity, event.getId());
        }
    }

}
