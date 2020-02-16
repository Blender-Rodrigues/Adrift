package ee.taltech.iti0200.physics;

import javax.vecmath.Vector2d;

public class Body {

    protected double mass;

    protected double inverseMass;
    protected Vector2d speed;
    protected Vector2d min;
    protected Vector2d max;
    protected boolean moved;
    private boolean collideable;

    public Body(double mass, Vector2d min, Vector2d max, boolean collideable) {
        this.mass = mass;
        this.speed = new Vector2d(0.0, 0.0);
        this.inverseMass = 1 / mass;
        this.min = min;
        this.max = max;
        this.collideable = collideable;
    }

    public Body(double mass, Vector2d size, Vector2d position, boolean collideable, boolean usingPositionAndSize) {
        this.mass = mass;
        this.speed = new Vector2d(0.0, 0.0);
        this.inverseMass = 1 / mass;
        this.collideable = collideable;
        this.min = new Vector2d();
        this.max = new Vector2d();
        min.scaleAdd(-0.5, size, position);
        max.scaleAdd(0.5, size, position);
    }

    public void move(double timeToMove) {
        min.scaleAdd(timeToMove, speed, min);
        max.scaleAdd(timeToMove, speed, max);
    }

    public double getMass() {
        return mass;
    }

    public Vector2d getMin() {
        return min;
    }

    public Vector2d getMax() {
        return max;
    }

    public boolean isCollideable() {
        return collideable;
    }

}
