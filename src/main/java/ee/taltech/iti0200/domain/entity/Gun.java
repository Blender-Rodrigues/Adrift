package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class Gun extends Entity {

    private static final long serialVersionUID = 1L;

    private Living owner;

    protected long cooldown = 0;
    protected int damage = 10;
    protected long fireRate = 90;
    protected double projectileSpeed = 10;

    public Gun(BoundingBox boundingBox) {
        super(0, boundingBox);
    }

    public Gun setOwner(Living owner) {
        this.owner = owner;
        this.boundingBox = owner.getBoundingBox();
        return this;
    }

    public boolean canShoot(long tick) {
        return cooldown <= tick;
    }

    public Projectile shoot(Vector direction, long tick) {
        if (owner == null) {
            throw new RuntimeException("Trying to shoot a gun without an owner");
        }

        cooldown = tick + fireRate;

        Vector speed = new Vector(direction);
        speed.normalize();
        speed.scale(projectileSpeed);

        return new Projectile(new Vector(owner.getBoundingBox().getCentre()), speed, damage, owner);
    }

}
