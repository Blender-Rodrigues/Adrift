package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import javax.vecmath.Vector2d;
import java.util.Arrays;
import java.util.List;

public class Terrain extends Entity {

    private static final Vector2d size = new Vector2d(2.0, 2.0);
    private static final double mass = Double.POSITIVE_INFINITY;

    public Terrain(Vector2d position) {
        super(makeTerrainEntity(position));
    }

    static private List<Body> makeTerrainEntity(Vector2d position) {
        Vector2d min = new Vector2d();
        Vector2d max = new Vector2d();
        min.scaleAdd(-0.5, size, position);
        max.scaleAdd(0.5, size, position);
        return Arrays.asList(new Body(mass, min, max));
    }
}
