package ee.taltech.iti0200.domain.entity.equipment;

import ee.taltech.iti0200.domain.entity.BlastProjectile;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class SpecialGun extends Gun {

    private static final long serialVersionUID = 1L;

    public SpecialGun(BoundingBox boundingBox) {
        super(boundingBox);
        fireRate = 80;
        damage = 50;
    }

    protected Projectile createProjectile(Vector position, Vector speed) {
        return new BlastProjectile(position, speed, damage, owner);
    }

}
