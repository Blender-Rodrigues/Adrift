package ee.taltech.iti0200.domain.entity.equipment;

import ee.taltech.iti0200.domain.entity.PlasmaProjectile;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class FastGun extends Gun {

    private static final long serialVersionUID = 1L;

    public FastGun(BoundingBox boundingBox) {
        super(boundingBox);
        fireRate = 20;
        damage = 6;
    }

    protected Projectile createProjectile(Vector position, Vector speed) {
        return new PlasmaProjectile(position, speed, damage, owner);
    }

}
