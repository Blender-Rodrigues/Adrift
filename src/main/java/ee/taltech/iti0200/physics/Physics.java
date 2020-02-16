package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.World;

import java.util.List;

public class Physics {

    private World world;

    public Physics(World world) {
        this.world = world;
    }

    public void step(long tick) {
        List<Body> movableBodies = world.getMovableBodies();
        List<Body> imMovableBodies = world.getImMovableBodies();
        moveBodies(movableBodies, world.getTimeStep());
        checkOutOfBounds();
    }

    private void checkOutOfBounds() {

    }

    private void moveBodies(List<Body> bodiesToMove, double timeStep) {
        for (Body body: bodiesToMove) {
            body.move(timeStep);
        }
    }

}
