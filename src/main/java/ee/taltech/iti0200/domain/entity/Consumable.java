package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.BoundingBox;

public class Consumable extends Entity {

    private static final long serialVersionUID = 1L;

    protected transient World world;

    public static final double MASS = 0;

    public Consumable(BoundingBox boundingBox, World world) {
        super(MASS, boundingBox);
        this.world = world;
    }

}
