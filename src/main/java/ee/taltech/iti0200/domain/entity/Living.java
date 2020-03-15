package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.BoundingBox;

public class Living extends Damageable {

    private static final long serialVersionUID = 1L;

    protected transient World world;

    public Living(double mass, BoundingBox boundingBox, World world, int health) {
        super(mass, boundingBox, health);
        this.world = world;
        this.movable = true;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Called for living things on every game tick
     */
    public void update(long tick) {

    }

}
