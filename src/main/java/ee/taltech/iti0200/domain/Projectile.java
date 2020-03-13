package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Projectile extends Entity {

    private static final Vector size = new Vector(0.5, 0.5);
    private int damage;

    public Projectile(Vector position, Vector speed, int damage) {
        super(new Body(0, size, position, true, true), true);
        this.speed = speed;
        this.damage = damage;
    }

    @Override
    public void onCollide(Body otherBody) {
        super.onCollide(otherBody);
        this.removed = true;
    }

    public int getDamage() {
        return damage;
    }

}
