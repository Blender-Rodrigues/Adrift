package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityCreateHandler implements Subscriber<CreateEntity> {

    private final Logger logger = LogManager.getLogger(EntityCreateHandler.class);

    private World world;
    private Score score;

    @Inject
    public EntityCreateHandler(World world, Score score) {
        this.world = world;
        this.score = score;
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
            if (entity instanceof Player) {
                score.addPlayer((Player) entity);
            }
        }

        world.addEntity(entity);

        logger.log(level, "Added {} to the world", entity);
    }

}
