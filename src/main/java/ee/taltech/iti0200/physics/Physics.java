package ee.taltech.iti0200.physics;

public class Physics {

    private World world;

    public Physics(World world) {
        this.world = world;
    }

    public void step() {
        world.moveBodies();
        checkOutOfBounds();
    }

    private void checkOutOfBounds() {

    }

}
