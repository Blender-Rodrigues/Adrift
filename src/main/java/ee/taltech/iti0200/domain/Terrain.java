package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import javax.vecmath.Vector2d;
import java.util.Arrays;

public class Terrain extends Entity {

    private static final Vector2d size = new Vector2d(2.0, 2.0);
    private static final double mass = Double.POSITIVE_INFINITY;

    public Terrain(Vector2d position) {
        super(Arrays.asList(new Body(mass, size, position, true)));;
    }

}
