package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class Projectile extends Entity implements DamageSource {

    private static final long serialVersionUID = 1L;
    private static final Vector SIZE = new Vector(0.5, 0.5);

    private int damage;
    private Living owner;

    public Projectile(Vector position, Vector speed, int damage, Living owner) {
        super(0, new BoundingBox(position, SIZE));
        this.speed = speed;
        this.damage = damage;
        this.owner = owner;
        this.movable = true;
        this.elasticity = 0;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public Living getOwner() {
        return owner;
    }

}
