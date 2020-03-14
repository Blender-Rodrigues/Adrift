package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Player extends Living {

    private static final Vector SIZE = new Vector(1.0, 1.0);
    private static final double MASS = 70.0;
    private static final double ELASTICITY = 0.25;
    private static final double FRICTION_COEFFICIENT = 0.99;
    private static final int JUMP_AMOUNT_LIMIT = 2;
    private static final double JUMP_DELTA_V = 10.0;
    private static final int MAX_HEALTH = 100;

    private int jumpsLeft;
    private Gun gun;

    public Player(Vector position, World world) {
        super(new Body(MASS, new Vector(SIZE), position, true, true), false, world);
        setElasticity(ELASTICITY);
        setJumpsLeft(JUMP_AMOUNT_LIMIT);
        setFrictionCoefficient(FRICTION_COEFFICIENT);
        gun = new Gun(boundingBox, 30, this);
        health = MAX_HEALTH;
    }

    public void shoot() {
        if (!gun.canShoot(world.getTime())) {
            return;
        }

        Projectile projectile = gun.shoot(speed, world.getTime());
        world.addBody(projectile, true);
    }

    public double getJumpDeltaV() {
        return JUMP_DELTA_V;
    }

    public void setJumpsLeft(int jumpsLeft) {
        this.jumpsLeft = jumpsLeft;
    }

    public int getJumpsLeft() {
        return this.jumpsLeft;
    }

    @Override
    public void onCollide(Body otherBody) {
        super.onCollide(otherBody);
        if (otherBody instanceof Terrain) {
            boolean verticalCollision = boundingBox.getOverLap(otherBody.getBoundingBox()).getY() == 0;
            boolean otherIsBelow = boundingBox.getCentre().getY() > otherBody.getBoundingBox().getCentre().getY();
            if (verticalCollision && otherIsBelow) {
                setJumpsLeft(JUMP_AMOUNT_LIMIT);
            }
        }
    }

}
