package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Terrain extends Entity {

    private static final Vector size = new Vector(2.0, 2.0);
    private static final double mass = Double.POSITIVE_INFINITY;
    private static final double elasticity = 0.9;

    public Terrain(Vector position) {
        super(new Body(mass, new Vector(size), position, true, true), true);
        setElasticity(elasticity);
    }

}
