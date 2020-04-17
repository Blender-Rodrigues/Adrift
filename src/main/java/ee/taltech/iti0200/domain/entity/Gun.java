package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;


public class Gun extends Equipment implements Rotatable {

    private static final long serialVersionUID = 1L;

    private Vector pointedAt = new Vector(1, 0);

    protected long cooldown = 0;
    protected int damage = 10;
    protected long fireRate = 90;
    protected double projectileSpeed = 3;

    public Gun(BoundingBox boundingBox) {
        super(boundingBox);
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
        cooldown = tick + fireRate;
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

    public Vector getRotation() {
        return pointedAt;
    }

    public void setRotation(Vector pointedAt) {
        this.pointedAt = new Vector(pointedAt);
        this.pointedAt.normalize();
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        if (isActive) super.render(shader, camera, tick);
    }
}
