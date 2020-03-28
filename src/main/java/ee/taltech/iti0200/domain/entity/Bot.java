package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.ai.Brain;
import ee.taltech.iti0200.ai.InertBrain;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Bot extends Living {

    private static final long serialVersionUID = 1L;

    private static final Vector SIZE = new Vector(1.5, 1.5);
    private static final double MASS = 70.0;
    private static final double ELASTICITY = 0.25;
    private static final double FRICTION_COEFFICIENT = 0.99;
    private static final int MAX_HEALTH = 100;

    private transient Brain brain;

    private Vector acceleration;
    private Gun gun;

    public Bot(Vector position, World world, Brain brain) {
        super(MASS, new BoundingBox(position, SIZE), world, MAX_HEALTH);
        this.brain = brain;
        this.elasticity = ELASTICITY;
        this.frictionCoefficient = FRICTION_COEFFICIENT;
        this.acceleration = new Vector(0.0, 0.0);
        this.movable = true;
    }

    public Gun getGun() {
        return gun;
    }

    public Bot setGun(Gun gun) {
        this.gun = gun;
        gun.setOwner(this);
        return this;
    }

    public Brain getBrain() {
        return brain;
    }

    public boolean canShoot(long tick) {
        return gun != null && gun.canShoot(tick);
    }

    public Vector getAcceleration() {
        return acceleration;
    }

    public void update(long tick) {
        brain.followGoal(tick);
    }

    /**
     * Make sure that the client side receives a copy of a not so smart brain after deserialization
     */
    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        brain = new InertBrain();
    }

}
