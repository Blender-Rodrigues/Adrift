package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Player extends Entity {

    private static final Vector size = new Vector(1.5, 1.5);
    private static final double mass = 70.0;
    private static final double elasticity = 0.25;
    private static final int jumpAmountLimit = 2;
    private static final double jumpDeltaV = 10.0;
    private int jumpsLeft;

    public Player(Vector position) {
        super(new Body(mass, new Vector(size), position, true, true), false);
        setElasticity(elasticity);
        setJumpsLeft(getJumpAmountLimit());
    }

    public int getJumpAmountLimit() {
        return jumpAmountLimit;
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

    public void onCollide(Body otherBody) {
        if (otherBody instanceof Terrain) {
            boolean verticalCollision = getBoundingBox().getOverLap(otherBody.getBoundingBox()).getY() == 0;
            boolean otherIsBelow = getBoundingBox().getCentre().getY() > otherBody.getBoundingBox().getCentre().getX();
            if (verticalCollision && otherIsBelow) {
                setJumpsLeft(getJumpAmountLimit());
            }
        }
    }

}
