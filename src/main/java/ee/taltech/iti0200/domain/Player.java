package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import javax.vecmath.Vector2d;
import java.util.Arrays;

public class Player extends Entity {

    private static final Vector2d size = new Vector2d(1.5, 1.5);
    private static final double mass = 70.0;

    public Player(Vector2d position) {
        super(Arrays.asList(new Body(mass, new Vector2d(size), position, true, true)), false);
    }
}
