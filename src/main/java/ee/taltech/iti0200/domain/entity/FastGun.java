package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;

public class FastGun extends Gun {

    private static final long serialVersionUID = 1L;

    public FastGun(BoundingBox boundingBox) {
        super(boundingBox);
        fireRate = 7;
    }

}
