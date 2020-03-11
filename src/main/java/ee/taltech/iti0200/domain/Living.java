package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import java.util.List;

public class Living extends Entity {
    public Living(List<Body> components, boolean collideable) {
        super(components, collideable);
    }

    public Living(Body component, boolean collideable) {
        super(component, collideable);
    }

    /**
     * Called for living things on every game tick
     */
    public void update(long tick) {

    }
}
