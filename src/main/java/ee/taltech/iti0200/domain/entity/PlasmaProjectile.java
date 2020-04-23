package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class PlasmaProjectile extends Projectile {

    private static final long serialVersionUID = 1L;
    private static final Vector SIZE = new Vector(0.3, 0.3);

    public PlasmaProjectile(Vector position, Vector speed, int damage, Living owner) {
        super(new BoundingBox(position, SIZE), speed, damage, owner);
    }

}
