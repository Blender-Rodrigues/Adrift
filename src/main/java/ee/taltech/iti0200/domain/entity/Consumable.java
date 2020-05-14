package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;

public class Consumable extends Loot {

    private static final long serialVersionUID = 1L;

    public static final double MASS = 0;

    public Consumable(BoundingBox boundingBox) {
        super(MASS, boundingBox);
    }

}
