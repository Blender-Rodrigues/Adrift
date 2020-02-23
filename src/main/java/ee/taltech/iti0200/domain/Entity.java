package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.AABB;
import ee.taltech.iti0200.physics.Body;

import javax.vecmath.Vector2d;
import java.util.List;

public class Entity extends Body {

    private List<Body> components;

    public Entity(List<Body> components, boolean collideable) {
        super(
            0.0,
            new Vector2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
            new Vector2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
                collideable
        );

        this.components = components;

        Vector2d min = new Vector2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Vector2d max = new Vector2d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (Body component: components) {
            this.mass += component.getMass();
            min = new Vector2d(
                Math.min(min.getX(), component.getBoundingBox().getMinX()),
                Math.min(min.getY(), component.getBoundingBox().getMinY())
            );
            max = new Vector2d(
                Math.max(max.getX(), component.getBoundingBox().getMaxX()),
                Math.max(max.getY(), component.getBoundingBox().getMaxX())
            );
        }

        this.boundingBox = new AABB(min, max);

        this.inverseMass = 1 / this.mass;
    }

    /**
     * Called for living things on every game tick
     */
    public void update(long tick) {

    }

    public void onCollide(Entity otherEntity) {

    }

}
