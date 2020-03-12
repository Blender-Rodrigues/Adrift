package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Player extends Living {

    private static final Vector size = new Vector(1.0, 1.0);
    private static final double mass = 70.0;
    private static final double elasticity = 0.25;
    private static final double frictionCoefficient = 0.99;
    private static final int jumpAmountLimit = 2;
    private static final double jumpDeltaV = 10.0;

    private int jumpsLeft;
    private Gun gun;

    public Player(Vector position) {
        super(new Body(mass, new Vector(size), position, true, true), false);
        setElasticity(elasticity);
        setJumpsLeft(jumpAmountLimit);
        setFrictionCoefficient(frictionCoefficient);
        gun = new Gun();
    }

    public Projectile shoot() {
        gun.shoot();

        Vector speed = new Vector(getSpeed());
        speed.normalize();
        speed.scale(gun.getProjectileSpeed());

        Vector position = new Vector(getBoundingBox().getCentre());

        return new Projectile(position, speed);
    }

    public void update(long tick, double timePassed) {
        gun.passTime(timePassed);
    }

    public boolean canShoot() {
        return gun.canShoot();
    }

    public double getJumpDeltaV() {
        return jumpDeltaV;
    }

    public void setJumpsLeft(int jumpsLeft) {
        this.jumpsLeft = jumpsLeft;
    }

    public int getJumpsLeft() {
        return this.jumpsLeft;
    }

    @Override
    public void onCollide(Body otherBody) {
        if (otherBody instanceof Terrain) {
            boolean verticalCollision = getBoundingBox().getOverLap(otherBody.getBoundingBox()).getY() == 0;
            boolean otherIsBelow = getBoundingBox().getCentre().getY() > otherBody.getBoundingBox().getCentre().getY();
            if (verticalCollision && otherIsBelow) {
                setJumpsLeft(jumpAmountLimit);
            }
        }
    }

}
