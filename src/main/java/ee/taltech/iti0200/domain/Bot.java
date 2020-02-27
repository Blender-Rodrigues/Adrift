package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import javax.vecmath.Vector2d;
import java.util.Arrays;
import java.util.Random;

public class Bot extends Entity {

    private static final Vector2d size = new Vector2d(1.5, 1.5);
    private static final double mass = 70.0;
    private static final Random random = new Random();

    public Bot(Vector2d position) {
        super(Arrays.asList(new Body(mass, size, position, true, true)), false);
    }

    public void update(long tick) {
        if (tick % 100 == 0) {
            speed.add(new Vector2d(random.nextDouble() * 2.0 - 1.0, 0));
        }
    }
}
