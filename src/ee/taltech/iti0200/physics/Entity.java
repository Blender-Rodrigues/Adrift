package ee.taltech.iti0200.physics;

import java.util.List;

public class Entity extends Body{
    private List<Body> components;

    public Entity(List<Body> components) {
        super(0.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        this.components = components;
        for (Body component: components) {
            this.mass += component.mass;
            this.xMin = Math.min(this.xMin, component.xMin);
            this.yMin = Math.min(this.yMin, component.yMin);
            this.xMax = Math.max(this.xMax, component.xMax);
            this.yMax = Math.max(this.yMax, component.yMax);
        }
        this.inverseMass = 1 / this.mass;
    }
}
