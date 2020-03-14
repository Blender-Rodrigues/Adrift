package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class Gun extends Entity {

    private static final double PROJECTILE_SPEED = 15;

    private long cooldown = 0;
    private int damage = 10;
    private long fireRate;
    private Living owner;

    public Gun(BoundingBox boundingBox, long fireRate, Living owner) {
        super(0, boundingBox, false);
        this.fireRate = fireRate;
        this.owner = owner;
    }

    public boolean canShoot(long tick) {
        return cooldown <= tick;
    }

    public Projectile shoot(Vector direction, long tick) {
        cooldown = tick + fireRate;

        Vector speed = new Vector(direction);
        speed.normalize();
        speed.scale(PROJECTILE_SPEED);

        return new Projectile(new Vector(boundingBox.getCentre()), speed, damage, owner);
    }

}
