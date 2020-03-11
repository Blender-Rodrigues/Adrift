package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

import java.util.Random;

public class Bot extends Entity {

    private static final Vector size = new Vector(1.5, 1.5);
    private static final double mass = 70.0;
    private static final Random random = new Random();
    private static final double elasticity = 0.25;
    private static final double frictionCoefficient = 0.99;
    private World world;

    private Vector acceleration;

    public Bot(Vector position, World world) {
        super(new Body(mass, size, position, true, true), false);
        setElasticity(elasticity);
        setFrictionCoefficient(frictionCoefficient);
        acceleration = new Vector(0.0, 0.0);
        this.world = world;
    }

    public void update(long tick) {
        if (tick % 10 == 0) {
            move();
            look();
        }
    }

    private void look() {

    }

    private void move() {
        acceleration.add(new Vector(random.nextDouble() - 0.5, 0));
        acceleration.scale(0.9);
        speed.add(acceleration);
    }

}
