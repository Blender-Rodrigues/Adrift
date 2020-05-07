package ee.taltech.iti0200.domain.entity.equipment;

import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.entity.Rotatable;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;


public class Gun extends Equipment implements Rotatable {

    private static final long serialVersionUID = 1L;

    private Vector pointedAt = new Vector(1, 0);
    protected static final Vector SIZE = new Vector(2, 2);

    protected long cooldown = 0;
    protected int damage;
    protected long rechargeRate;
    protected double projectileSpeed;

    public Gun(Vector position) {
        this(new BoundingBox(position, SIZE));
    }

    public Gun(BoundingBox boundingBox) {
        super(boundingBox);
        damage = 10;
        rechargeRate = 20;
        projectileSpeed = 8;
    }

    public Gun setOwner(Living owner) {
        this.owner = owner;
        this.boundingBox = owner.getBoundingBox();
        return this;
    }

    public boolean canShoot(long tick) {
        return cooldown <= tick;
    }

    public void resetCooldown(long tick) {
        cooldown = tick + rechargeRate;
    }

    public Projectile shoot(Vector direction, long tick) {
        if (owner == null) {
            throw new RuntimeException("Trying to shoot a gun without an owner");
        }

        cooldown = tick + rechargeRate;

        Vector speed = new Vector(direction);
        speed.normalize();
        speed.scale(projectileSpeed);

        return createProjectile(new Vector(owner.getBoundingBox().getCentre()), speed);
    }

    public Vector getRotation() {
        return pointedAt;
    }

    public void setRotation(Vector pointedAt) {
        this.pointedAt = new Vector(pointedAt);
        this.pointedAt.normalize();
    }

    protected Projectile createProjectile(Vector position, Vector speed) {
        return new Projectile(position, speed, damage, owner);
    }

}
