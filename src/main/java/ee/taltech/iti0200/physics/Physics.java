package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.Entity;
import ee.taltech.iti0200.domain.Player;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.input.Input;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.vecmath.Vector2d;
import java.util.List;

public class Physics implements Component {

    private World world;
    private Logger logger;

    private static final Vector2d GRAVITY = new Vector2d(0, -9.81);

    public Physics(World world) {
        logger = LogManager.getLogger(Physics.class);
        this.world = world;
    }

    @Override
    public void update(long tick) {
        List<Entity> movableBodies = world.getMovableBodies();
        List<Entity> imMovableBodies = world.getImMovableBodies();
        moveBodies(movableBodies, world.getTimeStep());
        checkForCollisions(movableBodies, imMovableBodies);
        applyGravity(movableBodies);
    }

    private void applyGravity(List<Entity> entities) {
        Vector2d accelerateDelta = new Vector2d(GRAVITY);
        accelerateDelta.scale(world.getTimeStep());
        for (Entity entity: entities) {
            entity.accelerate(accelerateDelta);
        }
    }

    private void checkForCollisions(List<Entity> movingBodies, List<Entity> stationaryBodies) {
        for (Entity movingBody: movingBodies) {
            for (Entity stationaryBody: stationaryBodies) {
                if (!movingBody.isCollideable() && !stationaryBody.isCollideable()) {
                    continue;
                }
                if (movingBody.intersects(stationaryBody)) {
                    movingBody.onCollide(stationaryBody);
                    resolveCollision(movingBody, stationaryBody);
                }
            }
        }
    }

    private void resolveCollision(Body movingBody, Body stationaryBody) {
        if (movingBody.getClass() == Player.class) {
            logger.debug("Player at: " + movingBody.getBoundingBox().getCentre());
            logger.debug("Collided with: " + stationaryBody.getClass());
            logger.debug("At: " + stationaryBody.getBoundingBox().getCentre());
            logger.debug("");
        }
        Vector2d overLap = movingBody.getBoundingBox().getOverLap(stationaryBody.getBoundingBox());
        double toMoveX = (overLap.getX() < 0) ?  overLap.getX() : 0;
        double toMoveY = (overLap.getY() < 0) ?  overLap.getY() : 0;

        double directionX = (
            movingBody.getBoundingBox().getCentre().getX()
            > stationaryBody.getBoundingBox().getCentre().getX()
        ) ? -1 : 1;

        double directionY = (
            movingBody.getBoundingBox().getCentre().getY()
            > stationaryBody.getBoundingBox().getCentre().getY()
        ) ? -1 : 1;

        movingBody.move(new Vector2d(toMoveX * directionX, toMoveY * directionY));

        if (overLap.getX() < 0) {
            movingBody.setXSpeed(0);
        }

        if (overLap.getY() < 0) {
            movingBody.setYSpeed(0);
        }
    }

    private void moveBodies(List<Entity> bodiesToMove, double timeStep) {
        for (Entity body: bodiesToMove) {
            body.move(timeStep);
        }
    }

}
