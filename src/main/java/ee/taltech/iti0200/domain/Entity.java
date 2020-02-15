package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import javax.vecmath.Vector2d;
import java.util.List;

public class Entity extends Body {

    private List<Body> components;

    public Entity(List<Body> components) {
        super(
            0.0,
            new Vector2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
            new Vector2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)
        );

        this.components = components;

        for (Body component: components) {
            this.mass += component.getMass();
            min = new Vector2d(
                Math.min(min.getX(), component.getMin().getX()),
                Math.min(min.getY(), component.getMin().getY())
            );
            max = new Vector2d(
                Math.max(max.getX(), component.getMax().getX()),
                Math.max(max.getY(), component.getMax().getY())
            );
        }

        this.inverseMass = 1 / this.mass;
    }

    /**
     * Called for living things on every game tick
     */
    public void update(long tick) {

    }

}
