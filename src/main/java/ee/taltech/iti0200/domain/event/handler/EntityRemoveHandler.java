package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

public class EntityRemoveHandler implements Subscriber<RemoveEntity> {

    private final Logger logger = LogManager.getLogger(EntityRemoveHandler.class);

    private World world;
    private EventBus eventBus;

    @Inject
    public EntityRemoveHandler(World world, EventBus eventBus) {
        this.world = world;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(RemoveEntity event) {
        Entity entity = world.getEntity(event.getId());
        if (entity == null) {
            logger.warn("Entity {} already removed", event.getId());
            return;
        }

        world.removeEntity(entity);

        if (entity instanceof Bot) {
            ((Bot) entity).getBrain().kill();
        }

        if (entity instanceof Projectile) {
            logger.debug("Removed {} from the world", entity);
        } else {
            logger.info("Removed {} from the world", entity);
        }
    }

}
