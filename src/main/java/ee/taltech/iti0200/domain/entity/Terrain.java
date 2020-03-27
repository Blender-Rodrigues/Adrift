package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class Terrain extends Damageable {

    private static final long serialVersionUID = 1L;

    public static final double TERRAIN_BLOCK_RESOLUTION = 4;
    public static final double TERRAIN_BLOCK_SIZE = 1; // Layout class expect this size to be 1

    private static final Vector SIZE = new Vector(TERRAIN_BLOCK_SIZE, TERRAIN_BLOCK_SIZE);
    private static final double MASS = Double.POSITIVE_INFINITY;
    private static final double ELASTICITY = 0.9;
    private static final double FRICTION_COEFFICIENT = 0.9;
    private static final int MAX_HEALTH = 30;

    public Terrain(Vector position) {
        super(MASS, new BoundingBox(position.rounded(), SIZE), MAX_HEALTH);
        this.elasticity = ELASTICITY;
        this.frictionCoefficient = FRICTION_COEFFICIENT;
        this.collideable = true;
    }

}
