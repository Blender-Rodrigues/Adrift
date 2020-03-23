package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityCreateHandler implements Subscriber<CreateEntity> {

    private final Logger logger = LogManager.getLogger(EntityCreateHandler.class);

    private World world;

    public EntityCreateHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(CreateEntity event) {
        Entity entity = event.getEntity();
        if (world.getEntity(entity.getId()) != null) {
            logger.warn("Entity {} already created", entity);
            return;
        }

        Level level = Level.DEBUG;

        if (entity instanceof Living) {
            ((Living) entity).setWorld(world);
            level = Level.INFO;
        }

        world.addEntity(entity);

        logger.log(level, "Added {} to the world", entity);
    }

}
