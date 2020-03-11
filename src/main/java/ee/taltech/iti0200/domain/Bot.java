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

    public Bot(Vector position) {
        super(new Body(mass, size, position, true, true), false);
        setElasticity(elasticity);
        setFrictionCoefficient(frictionCoefficient);
    }

    public void update(long tick) {
        if (tick % 100 == 0) {
            speed.add(new Vector(random.nextDouble() * 2.0 - 1.0, 0));
        }
    }

}
