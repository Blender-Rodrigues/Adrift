package ee.taltech.iti0200.physics;

import javax.vecmath.Vector2d;

public class Box extends Body {

    public Box(double mass, Vector2d min, Vector2d max, boolean collideable) {
        super(mass, min, max, collideable);
    }

}
