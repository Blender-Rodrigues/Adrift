package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
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
        Entity entity = world.getEntity(event.getId());
        if (entity == null) {
            logger.debug("Entity {} already removed", event.getId());
            return;
        }

        world.removeEntity(entity);

        if (entity instanceof Projectile) {
            logger.debug("Removed {} from the world", entity);
        } else {
            logger.info("Removed {} from the world", entity);
        }
    }

}
