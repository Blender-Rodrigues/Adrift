package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

import java.util.Arrays;
import java.util.List;

public class Entity extends Body {

    private List<Body> components;

    public boolean isOnFloor() {
        return onFloor;
    }

    public void setOnFloor(boolean onFloor) {
        this.onFloor = onFloor;
    }

    private boolean onFloor;

    public Entity(List<Body> components, boolean collideable) {
        super(
            0.0,
            new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
            new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
            collideable
        );

        this.components = components;

        Vector min = new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Vector max = new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

        for (Body component: components) {
            this.mass += component.getMass();
            min = new Vector(
                Math.min(min.getX(), component.getBoundingBox().getMinX()),
                Math.min(min.getY(), component.getBoundingBox().getMinY())
            );
            max = new Vector(
                Math.max(max.getX(), component.getBoundingBox().getMaxX()),
                Math.max(max.getY(), component.getBoundingBox().getMaxY())
            );
        }

        this.boundingBox = new BoundingBox(min, max);

        this.inverseMass = 1 / this.mass;
    }

    public Entity(Body component, boolean collideable) {
        super(
            component.getMass(),
            component.getBoundingBox(),
            collideable
        );

        this.components = Arrays.asList(component);
    }

    /**
     * Called for living things on every game tick
     */
    public void update(long tick) {

    }

    public List<Body> getComponents() {
        return components;
    }

    public void initializeGraphics() {
        components.forEach(Body::initializeGraphics);
    }

    public void render(Shader shader, Camera camera) {
        components.forEach(component -> component.render(shader, camera));
    }

}
