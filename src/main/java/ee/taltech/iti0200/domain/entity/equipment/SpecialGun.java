package ee.taltech.iti0200.domain.entity.equipment;

import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.physics.BoundingBox;

public class SpecialGun extends Gun {

    private static final long serialVersionUID = 1L;

    public SpecialGun(BoundingBox boundingBox) {
        super(boundingBox);
        projectileSpeed = 8;
        damage = 30;
    }

}
