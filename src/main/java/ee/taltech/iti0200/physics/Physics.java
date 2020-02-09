package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.World;

public class Physics {

    private World world;

    public Physics(World world) {
        this.world = world;
    }

    public void step(long tick) {
        world.moveBodies();
        checkOutOfBounds();
    }

    private void checkOutOfBounds() {

    }

}
