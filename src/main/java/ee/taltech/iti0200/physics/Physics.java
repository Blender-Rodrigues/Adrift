package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.Entity;
import ee.taltech.iti0200.domain.World;

import java.util.List;

public class Physics {

    private World world;

    public Physics(World world) {
        this.world = world;
    }

    public void step(long tick) {
        List<Entity> movableBodies = world.getMovableBodies();
        List<Entity> imMovableBodies = world.getImMovableBodies();
        moveBodies(movableBodies, world.getTimeStep());
        checkForCollisions(movableBodies, imMovableBodies);
    }

    private void checkForCollisions(List<Entity> movingBodies, List<Entity> stationaryBodies) {
        for (Entity movingBody: movingBodies) {
            for (Entity stationaryBody: stationaryBodies) {
                if (!movingBody.isCollideable() && !stationaryBody.isCollideable()) {
                    continue;
                }
                if (movingBody.getMin().getX() < stationaryBody.getMax().getX() &&
                        movingBody.getMax().getX() > stationaryBody.getMin().getX() &&
                        movingBody.getMin().getY() < stationaryBody.getMax().getY() &&
                        movingBody.getMax().getY() > stationaryBody.getMin().getY()) {
                    movingBody.onCollide(stationaryBody);
                }
            }
        }
    }

    private void moveBodies(List<Entity> bodiesToMove, double timeStep) {
        for (Entity body: bodiesToMove) {
            body.move(timeStep);
        }
    }

}
