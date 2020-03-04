package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Terrain extends Entity {

    private static final Vector size = new Vector(2.00, 2.00);
    private static final double mass = Double.POSITIVE_INFINITY;
    private static final double elasticity = 0.9;

    public Terrain(Vector position) {
        super(new Body(mass, new Vector(size), Vector.roundVector(position), true, true), true);
        setElasticity(elasticity);
    }

    public int getIntegerWidth() {
        return (int) getBoundingBox().getSize().getX() * 100;
    }

}
