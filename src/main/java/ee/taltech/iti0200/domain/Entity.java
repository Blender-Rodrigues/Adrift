package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import java.util.List;

public class Entity extends Body {

    private List<Body> components;

    public Entity(List<Body> components) {
        super(0.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);

        this.components = components;

        for (Body component: components) {
            this.mass += component.getMass();
            this.xMin = Math.min(this.xMin, component.getxMin());
            this.yMin = Math.min(this.yMin, component.getyMin());
            this.xMax = Math.max(this.xMax, component.getxMax());
            this.yMax = Math.max(this.yMax, component.getyMax());
        }

        this.inverseMass = 1 / this.mass;
    }

    /**
     * Called for living things on every game tick
     */
    public void update(long tick) {

    }

}
