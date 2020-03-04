package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.Entity;
import ee.taltech.iti0200.domain.Player;
import ee.taltech.iti0200.domain.Terrain;
import ee.taltech.iti0200.domain.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Physics implements Component {

    private World world;
    private Logger logger;

    private static final Vector GRAVITY = new Vector(0, -9.81);
    private static final double NO_BOUNCE_SPEED_LIMIT = 1;

    public Physics(World world) {
        logger = LogManager.getLogger(Physics.class);
        this.world = world;
    }

    @Override
    public void update(long tick) {
        List<Entity> movableBodies = world.getMovableBodies();
        List<Entity> imMovableBodies = world.getImMovableBodies();
        Map<Vector, Terrain> terrainMap = world.getTerrainMap();
        for (Entity player: movableBodies) {
            if (player instanceof Player) {
                logger.debug("Player at: " + player.getBoundingBox().getCentre());
            }
        }
        checkForFloor(movableBodies, terrainMap);
        moveBodies(movableBodies, world.getTimeStep());
        checkForCollisions(movableBodies, imMovableBodies);
        applyGravity(movableBodies);
    }

    private void checkForFloor(List<Entity> movingBodies, Map<Vector, Terrain> terrainMap) {
        for (Entity moving: movingBodies) {
            double minX = ((int) (moving.getBoundingBox().getMinX() * 100)) / 100d;
            double centreX = ((int) (moving.getBoundingBox().getCentre().getX() * 100)) / 100d;
            double maxX = ((int) (moving.getBoundingBox().getMaxX() * 100)) / 100d;
            double minY = moving.getBoundingBox().getMinY();

            boolean intersects = terrainMap.containsKey(new Vector(minX, minY))
                || terrainMap.containsKey(new Vector(centreX, minY))
                || terrainMap.containsKey(new Vector(maxX, minY));

            moving.setOnFloor(intersects);
        }
    }

    private void applyGravity(List<Entity> entities) {
        Vector accelerateDelta = new Vector(GRAVITY);
        accelerateDelta.scale(world.getTimeStep());
        entities.stream()
            .filter(entity -> !entity.isOnFloor())
            .forEach(entity -> entity.accelerate(accelerateDelta));
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

    private void resolveCollision(Entity movingBody, List<Body> collidingBodies) {
        if (collidingBodies.size() <= 0) {
            return;
        }
        Vector collisionElasticity = getStrategyForResolvingCollision(
            movingBody,
            collidingBodies,
            new Vector(0, 0),
            new Vector(0, 0)
        );
        double verticalSpeedAfterCollision = updateBodySpeedAfterCollision(movingBody, collisionElasticity);
        if (verticalSpeedAfterCollision >= 0d && verticalSpeedAfterCollision < NO_BOUNCE_SPEED_LIMIT) {
            movingBody.setYSpeed(0d);
            movingBody.setOnFloor(true);
        } else {
            movingBody.setOnFloor(false);
        }
    }

    private double updateBodySpeedAfterCollision(Body movingBody, Vector collisionElasticity) {
        collisionElasticity.scale(movingBody.getElasticity());
        if (collisionElasticity.getX() != 0) {
            movingBody.setXSpeed(- movingBody.getSpeed().getX() * collisionElasticity.getX());
        }
        if (collisionElasticity.getY() != 0) {
            movingBody.setYSpeed(- movingBody.getSpeed().getY() * collisionElasticity.getY());
        }
        return movingBody.getSpeed().getY();
    }

    private Vector getStrategyForResolvingCollision(
        Body movingBody,
        List<Body> collidingBodies,
        Vector movedSoFar,
        Vector elasticitySoFar
    ) {
        // Get all possible ways of resolving the collision and how good those ways are.
        List<Vector> resolveStrategies = getResolveStrategies(movingBody, collidingBodies);
        List<Vector> resolveStrategyResults = getResolveStrategyResults(
            movingBody,
            collidingBodies,
            resolveStrategies
        );

        // Get the best way of resolving the collision.
        int bestResolveStrategyIndex = getBestResolveStrategyIndex(resolveStrategyResults);
        Vector bestResolveStrategy = getBestResolveStrategy(bestResolveStrategyIndex, resolveStrategies);

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

    private Vector getNewElasticityOfCollision(
        Vector elasticitySoFar,
        Vector movedSoFar,
        Vector currentMove,
        double currentElasticity
    ) {
        // Get the average elasticity of the collision with respect to how much the body has been moved in each direction.
        Vector totalElasticityValue = new Vector(elasticitySoFar);
        totalElasticityValue.elementWiseMultiple(movedSoFar);

        Vector currentMoveElasticityValue = new Vector();
        Vector movedAfterThisCollision = new Vector();

        // Calculate the change in weighted elasticity with the current move.
        currentMoveElasticityValue.scale(currentElasticity, currentMove);
        // Add the change to the previous weighted elasticity.
        totalElasticityValue.add(currentMoveElasticityValue);
        movedAfterThisCollision.add(movedSoFar, currentMove);
        // Divide the weighted elasticity with how much the body has been moved, to get the average elasticity in each direction.
        totalElasticityValue.elementWiseDivide(movedAfterThisCollision);
        return totalElasticityValue;
    }

    private double getCollidingBodyElasticity(int bestResolveStrategyIndex, List<Body> collidingBodies) {
        return collidingBodies.get(bestResolveStrategyIndex / 2).getElasticity();
    }

    private List<Body> getBodiesThatAreStillColliding(Body movingBody, List<Body> collidingBodies) {
        return collidingBodies.stream()
            .filter(body -> body.intersects(movingBody))
            .collect(Collectors.toList());
    }

    private Vector getBestResolveStrategy(int bestIndex, List<Vector> resolveStrategies) {
        if (bestIndex % 2 == 0) {
            return new Vector(resolveStrategies.get(bestIndex / 2).getX(), 0);
        }
        return new Vector(0, resolveStrategies.get(bestIndex / 2).getY());
    }

    private int getBestResolveStrategyIndex(
        List<Vector> resolveStrategyResults
    ) {
        int bestIndex = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < resolveStrategyResults.size(); i++) {
            Vector resolveStrategyResult = resolveStrategyResults.get(i);
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

    private List<Vector> getResolveStrategyResults(
        Body movingBody,
        List<Body> collidingBodies,
        List<Vector> resolveStrategies
    ) {
        List<Vector> resolveStrategyResults = new ArrayList<>();
        double initialOverLap = getTotalOverLap(movingBody, collidingBodies);
        for (Vector resolveStrategy: resolveStrategies) {
            // Try moving the body along the x axis and find the overlap after that move.
            movingBody.move(new Vector(resolveStrategy.getX(), 0));
            double xMoveOverLap = getTotalOverLap(movingBody, collidingBodies);

            // Move the body back along the x axis and try moving in the y axis.
            movingBody.move(new Vector(- resolveStrategy.getX(), resolveStrategy.getY()));

            // Get the overlap and move body back along y axis.
            double yMoveOverLap = getTotalOverLap(movingBody, collidingBodies);
            movingBody.move(new Vector(0, - resolveStrategy.getY()));

            // Calculate efficiencies of each move by dividing the change in overlap by how much movement was necessary for that change.
            double xMoveEfficiency = (initialOverLap - xMoveOverLap) / Math.abs(resolveStrategy.getX());
            double yMoveEfficiency = (initialOverLap - yMoveOverLap) / Math.abs(resolveStrategy.getY());
            resolveStrategyResults.add(new Vector(xMoveEfficiency, yMoveEfficiency));
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

    private List<Vector> getResolveStrategies(Body movingBody, List<Body> collidingBodies) {
        List<Vector> resolveStrategies = new ArrayList<>();
        for (Body collidingBody: collidingBodies) {
            Vector overLap = movingBody.getBoundingBox().getOverLap(collidingBody.getBoundingBox());

            double directionX = movingBody.getBoundingBox().getCentreXDirection(collidingBody.getBoundingBox());
            double directionY = movingBody.getBoundingBox().getCentreYDirection(collidingBody.getBoundingBox());

            Vector resolveStrategy = new Vector(
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
