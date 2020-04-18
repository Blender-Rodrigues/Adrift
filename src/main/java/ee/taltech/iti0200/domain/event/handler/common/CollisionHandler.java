package ee.taltech.iti0200.domain.event.handler.common;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ee.taltech.iti0200.domain.entity.Player.JUMP_AMOUNT_LIMIT;

public class CollisionHandler implements Subscriber<EntityCollide> {

    private final Logger logger = LogManager.getLogger(CollisionHandler.class);

    protected World world;

    @Inject
    public CollisionHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(EntityCollide event) {
        Entity entity = event.getEntity();
        if (world.getEntity(entity.getId()) == null) {
            logger.trace("Entity {} is not in the world", entity);
            event.stop();
            return;
        }

        Entity other = event.getOther();
        if (world.getEntity(other.getId()) == null) {
            logger.trace("Entity {} is not in the world", other);
            event.stop();
            return;
        }

        logger.trace("{} collided with {}", entity, other);

        if (entity instanceof Player && other instanceof Terrain) {
            playerHitGround((Player) entity, (Terrain) other);
        }
    }

    private void playerHitGround(Player player, Terrain other) {
        boolean verticalCollision = player.getBoundingBox().getOverLap(other.getBoundingBox()).getY() == 0;
        boolean otherIsBelow = player.getBoundingBox().getCentre().getY() > other.getBoundingBox().getCentre().getY();
        if (verticalCollision && otherIsBelow) {
            player.setJumpsLeft(JUMP_AMOUNT_LIMIT);
        }
    }

}
