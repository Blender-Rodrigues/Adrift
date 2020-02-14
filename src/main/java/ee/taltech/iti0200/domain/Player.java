package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import javax.vecmath.Vector2d;
import java.util.Arrays;
import java.util.List;

public class Player extends Entity{

    private static final Vector2d size = new Vector2d(1.5, 1.5);
    private static final double mass = 70.0;

    public Player(Vector2d position) {
        super(makePlayerEntity(position));
    }

    static private List<Body> makePlayerEntity(Vector2d position) {
        Vector2d min = new Vector2d();
        Vector2d max = new Vector2d();
        min.scaleAdd(-0.5, size, position);
        max.scaleAdd(0.5, size, position);
        return Arrays.asList(new Body(mass, min, max));
    }
}
