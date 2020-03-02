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
        for (Entity player: movableBodies) {
            if (player.getClass() == Player.class) {
                logger.debug("Player at: " + player.getBoundingBox().getCentre());
            }
        }
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
                Vector2d collisionElasticity = resolveCollision(
                    movingBody,
                    collidingEntities,
                    new Vector2d(0, 0),
                    new Vector2d(0, 0)
                );
                updateBodySpeedAfterCollision(movingBody, collisionElasticity);
            }
        }
    }

    private void updateBodySpeedAfterCollision(Body movingBody, Vector2d collisionElasticity) {
        collisionElasticity.scale(movingBody.getElasticity());
        if (collisionElasticity.getX() != 0) {
            movingBody.setXSpeed(- movingBody.getSpeed().getX() * collisionElasticity.getX());
        }
        if (collisionElasticity.getY() != 0) {
            movingBody.setYSpeed(- movingBody.getSpeed().getY() * collisionElasticity.getY());
        }
    }

    private Vector2d resolveCollision(
        Body movingBody,
        List<Body> collidingBodies,
        Vector2d movedSoFar,
        Vector2d elasticitySoFar
    ) {
        List<Vector2d> resolveStrategies = getResolveStrategies(movingBody, collidingBodies);
        List<Vector2d> resolveStrategyResults = getResolveStrategyResults(
            movingBody,
            collidingBodies,
            resolveStrategies
        );
        int bestResolveStrategyIndex = getBestResolveStrategyIndex(resolveStrategies, resolveStrategyResults);
        Vector2d bestResolveStrategy = getBestResolveStrategy(bestResolveStrategyIndex, resolveStrategies);
        double collidingBodyElasticity = getCollidingBodyElasticity(bestResolveStrategyIndex, collidingBodies);
        movingBody.move(bestResolveStrategy);
        elasticitySoFar = getNewElasticityOfCollision(elasticitySoFar, movedSoFar, bestResolveStrategy, collidingBodyElasticity);
        movedSoFar.add(bestResolveStrategy);
        collidingBodies = getBodiesThatAreStillColliding(movingBody, collidingBodies);
        double totalOverLap = getTotalOverLap(movingBody, collidingBodies);
        if (totalOverLap != 0) {
            return resolveCollision(movingBody, collidingBodies, movedSoFar, elasticitySoFar);
        }
        return elasticitySoFar;
    }

    private Vector2d getNewElasticityOfCollision(
        Vector2d elasticitySoFar,
        Vector2d movedSoFar,
        Vector2d currentMove,
        double currentElasticity
    ) {
        Vector2d totalElasticityValue = elementWiseMultiple(elasticitySoFar, movedSoFar);
        Vector2d currentMoveElasticityValue = new Vector2d();
        Vector2d movedAfterThisCollision = new Vector2d();
        currentMoveElasticityValue.scale(currentElasticity, currentMove);
        totalElasticityValue.add(currentMoveElasticityValue);
        movedAfterThisCollision.add(movedSoFar, currentMove);
        return elementWiseDivide(totalElasticityValue, movedAfterThisCollision);
    }

    private Vector2d elementWiseDivide(Vector2d toDivide, Vector2d divisor) {
        double newX = (divisor.getX() == 0) ? 0 : toDivide.getX() / divisor.getX();
        double newY = (divisor.getY() == 0) ? 0 : toDivide.getY() / divisor.getY();
        return new Vector2d(newX, newY);
    }

    private Vector2d elementWiseMultiple(Vector2d vector1, Vector2d vector2) {
        return new Vector2d(
            vector1.getX() * vector2.getX(),
            vector1.getY() * vector2.getY()
        );
    }

    private double getCollidingBodyElasticity(int bestResolveStrategyIndex, List<Body> collidingBodies) {
        return collidingBodies.get(bestResolveStrategyIndex / 2).getElasticity();
    }

    private List<Body> getBodiesThatAreStillColliding(Body movingBody, List<Body> collidingBodies) {
        List<Body> stillCollidingBodies = new ArrayList<>();
        for (Body collidingBody: collidingBodies) {
            if (movingBody.intersects(collidingBody)) {
                stillCollidingBodies.add(collidingBody);
            }
        }
        return stillCollidingBodies;
    }

    private Vector2d getBestResolveStrategy(int bestIndex, List<Vector2d> resolveStrategies) {
        if (bestIndex % 2 == 0) {
            return new Vector2d(resolveStrategies.get(bestIndex / 2).getX(), 0);
        }
        return new Vector2d(0, resolveStrategies.get(bestIndex / 2).getY());
    }

    private int getBestResolveStrategyIndex(
        List<Vector2d> resolveStrategies,
        List<Vector2d> resolveStrategyResults
    ) {
        int bestIndex = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < resolveStrategyResults.size(); i++) {
            Vector2d resolveStrategyResult = resolveStrategyResults.get(i);
            if (resolveStrategyResult.getX() > bestValue) {
                bestValue = resolveStrategyResult.getX();
                bestIndex = i * 2;
            }
            if (resolveStrategyResult.getY() > bestValue) {
                bestValue = resolveStrategyResult.getY();
                bestIndex = i * 2 + 1;
            }
        }
        return bestIndex;
    }

    private List<Vector2d> getResolveStrategyResults(
        Body movingBody,
        List<Body> collidingBodies,
        List<Vector2d> resolveStrategies
    ) {
        List<Vector2d> resolveStrategyResults = new ArrayList<>();
        double initialOverLap = getTotalOverLap(movingBody, collidingBodies);
        for (Vector2d resolveStrategy: resolveStrategies) {
            movingBody.move(new Vector2d(resolveStrategy.getX(), 0));
            double xMoveOverLap = getTotalOverLap(movingBody, collidingBodies);

            movingBody.move(new Vector2d(- resolveStrategy.getX(), resolveStrategy.getY()));

            double yMoveOverLap = getTotalOverLap(movingBody, collidingBodies);
            movingBody.move(new Vector2d(0, - resolveStrategy.getY()));

            double xMoveEfficiency = (initialOverLap - xMoveOverLap) / Math.abs(resolveStrategy.getX());
            double yMoveEfficiency = (initialOverLap - yMoveOverLap) / Math.abs(resolveStrategy.getY());
            resolveStrategyResults.add(new Vector2d(xMoveEfficiency, yMoveEfficiency));
        }
        return resolveStrategyResults;
    }

    private double getTotalOverLap(Body movingBody, List<Body> collidingBodies) {
        return collidingBodies.stream()
            .map(Body::getBoundingBox)
            .map(boundingBox -> movingBody.getBoundingBox().getOverLap(boundingBox))
            .map(overLap -> Math.abs(overLap.getX()) * Math.abs(overLap.getY()))
            .reduce(0D, Double::sum);
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

    private void moveBodies(List<Entity> bodiesToMove, double timeStep) {
        for (Entity body: bodiesToMove) {
            body.move(timeStep);
        }
    }

}
