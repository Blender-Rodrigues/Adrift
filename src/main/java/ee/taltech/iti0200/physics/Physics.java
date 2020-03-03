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
import java.util.stream.Collectors;

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
            if (player instanceof Player) {
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
        entities.forEach(entity -> entity.accelerate(accelerateDelta));
    }

    private void checkForCollisions(List<Entity> movingBodies, List<Entity> stationaryBodies) {
       for (Entity moving: movingBodies) {
           List<Body> colliding = stationaryBodies.stream()
               .filter(stationary -> stationary.isCollideable() || moving.isCollideable())
               .filter(moving::intersects)
               .peek(moving::onCollide)
               .map(entity -> (Body) entity)
               .collect(Collectors.toList());

           resolveCollision(moving, colliding);
        }
    }

    private void resolveCollision(Body movingBody, List<Body> collidingBodies) {
        if (collidingBodies.size() > 0) {
            Vector2d collisionElasticity = getStrategyForResolvingCollision(
                movingBody,
                collidingBodies,
                new Vector2d(0, 0),
                new Vector2d(0, 0)
            );
            updateBodySpeedAfterCollision(movingBody, collisionElasticity);
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

    private Vector2d getStrategyForResolvingCollision(
        Body movingBody,
        List<Body> collidingBodies,
        Vector2d movedSoFar,
        Vector2d elasticitySoFar
    ) {
        // Get all possible ways of resolving the collision and how good those ways are.
        List<Vector2d> resolveStrategies = getResolveStrategies(movingBody, collidingBodies);
        List<Vector2d> resolveStrategyResults = getResolveStrategyResults(
            movingBody,
            collidingBodies,
            resolveStrategies
        );

        // Get the best way of resolving the collision.
        int bestResolveStrategyIndex = getBestResolveStrategyIndex(resolveStrategies, resolveStrategyResults);
        Vector2d bestResolveStrategy = getBestResolveStrategy(bestResolveStrategyIndex, resolveStrategies);

        // Move the body according to the chosen way and updated vectors that store how the body has been moved earlier during the same collision resolution.
        double collidingBodyElasticity = getCollidingBodyElasticity(bestResolveStrategyIndex, collidingBodies);
        movingBody.move(bestResolveStrategy);
        elasticitySoFar = getNewElasticityOfCollision(elasticitySoFar, movedSoFar, bestResolveStrategy, collidingBodyElasticity);
        movedSoFar.add(bestResolveStrategy);

        // Check if the body is still colliding with something.
        collidingBodies = getBodiesThatAreStillColliding(movingBody, collidingBodies);
        double totalOverLap = getTotalOverLap(movingBody, collidingBodies);
        if (totalOverLap != 0) {
            return getStrategyForResolvingCollision(movingBody, collidingBodies, movedSoFar, elasticitySoFar);
        }
        return elasticitySoFar;
    }

    private Vector2d getNewElasticityOfCollision(
        Vector2d elasticitySoFar,
        Vector2d movedSoFar,
        Vector2d currentMove,
        double currentElasticity
    ) {
        // Get the average elasticity of the collision with respect to how much the body has been moved in each direction.
        Vector2d totalElasticityValue = elementWiseMultiple(elasticitySoFar, movedSoFar);

        Vector2d currentMoveElasticityValue = new Vector2d();
        Vector2d movedAfterThisCollision = new Vector2d();

        // Calculate the change in weighted elasticity with the current move.
        currentMoveElasticityValue.scale(currentElasticity, currentMove);
        // Add the change to the previous weighted elasticity.
        totalElasticityValue.add(currentMoveElasticityValue);
        movedAfterThisCollision.add(movedSoFar, currentMove);
        // Divide the weighted elasticity with how much the body has been moved, to get the average elasticity in each direction.
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
        return collidingBodies.stream()
            .filter(body -> body.intersects(movingBody))
            .collect(Collectors.toList());
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
            // Try moving the body along the x axis and find the overlap after that move.
            movingBody.move(new Vector2d(resolveStrategy.getX(), 0));
            double xMoveOverLap = getTotalOverLap(movingBody, collidingBodies);

            // Move the body back along the x axis and try moving in the y axis.
            movingBody.move(new Vector2d(- resolveStrategy.getX(), resolveStrategy.getY()));

            // Get the overlap and move body back along y axis.
            double yMoveOverLap = getTotalOverLap(movingBody, collidingBodies);
            movingBody.move(new Vector2d(0, - resolveStrategy.getY()));

            // Calculate efficiencies of each move by dividing the change in overlap by how much movement was necessary for that change.
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
