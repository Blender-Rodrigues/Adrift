package ee.taltech.iti0200.physics;

import javax.vecmath.Vector2d;

public class Body {

    protected double mass;

    protected double inverseMass;
    protected Vector2d speed;
    protected Vector2d min;
    protected Vector2d max;
    protected boolean moved;
    public Body(double mass, Vector2d min, Vector2d max) {
        this.mass = mass;
        this.speed = new Vector2d(0.0, 0.0);
        this.inverseMass = 1 / mass;
        this.min = min;
        this.max = max;
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

}
