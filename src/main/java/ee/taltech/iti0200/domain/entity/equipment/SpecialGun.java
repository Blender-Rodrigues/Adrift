package ee.taltech.iti0200.domain.entity.equipment;

import ee.taltech.iti0200.domain.entity.BlastProjectile;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class SpecialGun extends Gun {

    private static final long serialVersionUID = 1L;

    public SpecialGun(Vector position) {
        this(new BoundingBox(position, SIZE));
    }

    public SpecialGun(BoundingBox boundingBox) {
        super(boundingBox);
        damage = 50;
        rechargeRate = 0;
        projectileSpeed = 6;
    }

    protected Projectile createProjectile(Vector position, Vector speed) {
        return new BlastProjectile(position, speed, damage, owner);
    }

}
