package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Projectile extends Entity {

    private static final Vector size = new Vector(0.5, 0.5);
    private int damage;
    private Living owner;

    public Projectile(Vector position, Vector speed, int damage, Living owner) {
        super(new Body(0, size, position, true, true), true);
        this.speed = speed;
        this.damage = damage;
        this.owner = owner;
    }

    @Override
    public void onCollide(Body otherBody) {
        super.onCollide(otherBody);
        if (otherBody != owner) {
            this.removed = true;
        }
    }

    public int getDamage() {
        return damage;
    }

    public Living getOwner() {
        return owner;
    }

}
