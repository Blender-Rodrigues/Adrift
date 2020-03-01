package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.Entity;
import ee.taltech.iti0200.domain.Player;
import ee.taltech.iti0200.domain.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
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
            List<Body> collidingEntities = new ArrayList<>();
            for (Entity stationaryBody: stationaryBodies) {
                if (!movingBody.isCollideable() && !stationaryBody.isCollideable()) {
                    continue;
                }
                if (movingBody.intersects(stationaryBody)) {
                    collidingEntities.add(stationaryBody);
                    movingBody.onCollide(stationaryBody);
                }
            }
            if (collidingEntities.size() > 0) {
                resolveCollision(movingBody, collidingEntities);
            }
        }
    }

    private void resolveCollision(Body movingBody, List<Body> collidingBodies) {
        List<Vector2d> resolveStrategies = getResolveStrategies(movingBody, collidingBodies);
    }

    private List<Vector2d> getResolveStrategies(Body movingBody, List<Body> collidingBodies) {
        List<Vector2d> resolveStrategies = new ArrayList<>();
        for (Body collidingBody: collidingBodies) {
            Vector2d overLap = movingBody.getBoundingBox().getOverLap(collidingBody.getBoundingBox());
            double directionX = (
                movingBody.getBoundingBox().getCentre().getX()
                    > collidingBody.getBoundingBox().getCentre().getX()
            ) ? -1 : 1;

            double directionY = (
                movingBody.getBoundingBox().getCentre().getY()
                    > collidingBody.getBoundingBox().getCentre().getY()
            ) ? -1 : 1;

            Vector2d resolveStrategy = new Vector2d(
                overLap.getX() * directionX,
                overLap.getY() * directionY
            );
            resolveStrategies.add(resolveStrategy);
        }
        return resolveStrategies;
    }

    private void resolveCollision(Body movingBody, Body stationaryBody) {
        Vector2d overLap = movingBody.getBoundingBox().getOverLap(stationaryBody.getBoundingBox());

        boolean xOverLap = overLap.getX() < 0;
        boolean yOverLap = overLap.getY() < 0;
        boolean xSmallerOverLap = overLap.getX() > overLap.getY();
        boolean ySmallerOverLap = overLap.getY() > overLap.getX();
        boolean xCollision = xOverLap && xSmallerOverLap;
        boolean yCollision = yOverLap && ySmallerOverLap;
        double toMoveX = xCollision ? overLap.getX() : 0;
        double toMoveY = yCollision ? overLap.getY() : 0;

        double directionX = (
            movingBody.getBoundingBox().getCentre().getX()
            > stationaryBody.getBoundingBox().getCentre().getX()
        ) ? -1 : 1;

        double directionY = (
            movingBody.getBoundingBox().getCentre().getY()
            > stationaryBody.getBoundingBox().getCentre().getY()
        ) ? -1 : 1;

        movingBody.move(new Vector2d(toMoveX * directionX, toMoveY * directionY));

        if (xCollision) {
            movingBody.setXSpeed(
                - movingBody.getSpeed().getX()
                * movingBody.getElasticity()
                * stationaryBody.getElasticity()
            );
        }

        if (yCollision) {
            movingBody.setYSpeed(
                - movingBody.getSpeed().getY()
                * movingBody.getElasticity()
                * stationaryBody.getElasticity()
            );
        }
    }

    private void moveBodies(List<Entity> bodiesToMove, double timeStep) {
        for (Entity body: bodiesToMove) {
            body.move(timeStep);
        }
    }

}
