package ee.taltech.iti0200.domain;

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

    public void onCollide(Entity otherEntity) {
        double overLap;
        if (min.getX() < otherEntity.getMax().getX() &&
                max.getX() > otherEntity.getMin().getX()) {
            if (speed.getX() > 0) {
                overLap = otherEntity.getMin().getX() - max.getX();
            } else {
                overLap = otherEntity.getMax().getX() - min.getX();
            }
            move(new Vector2d(overLap, 0));
            speed = new Vector2d(0, speed.getY());
        } else {
            if (speed.getY() > 0) {
                overLap = otherEntity.getMin().getY() - max.getY();
            } else {
                overLap = otherEntity.getMax().getY() - min.getY();
            }
            move(new Vector2d(0, overLap));
            speed = new Vector2d(speed.getX(), 0);
        }
    }

}
