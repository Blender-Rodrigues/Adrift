package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class Gun extends Entity {

    private static final long serialVersionUID = 1L;

    private static final double PROJECTILE_SPEED = 15;

    private long cooldown = 0;
    private int damage = 10;
    private long fireRate;
    private Living owner;
    private Vector pointedAt;

    public Gun(BoundingBox boundingBox, long fireRate, Living owner) {
        super(0, boundingBox);
        this.fireRate = fireRate;
        this.owner = owner;
        this.pointedAt = new Vector(1, 0);
    }

    public boolean canShoot(long tick) {
        return cooldown <= tick;
    }

    public Projectile shoot(Vector direction, long tick) {
        cooldown = tick + fireRate;

        Vector speed = new Vector(direction);
        speed.normalize();
        speed.scale(PROJECTILE_SPEED);

        return new Projectile(new Vector(owner.getBoundingBox().getCentre()), speed, damage, owner);
    }

    public Vector getPointedAt() {
        return pointedAt;
    }

    public Living getOwner() {
        return owner;
    }

    public void setPointedAt(Vector pointedAt) {
        this.pointedAt = new Vector(pointedAt);
        this.pointedAt.normalize();
    }

}
