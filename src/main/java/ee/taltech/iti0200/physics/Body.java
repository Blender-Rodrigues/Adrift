package ee.taltech.iti0200.physics;

import javax.vecmath.Vector2d;

public class Body {

    protected double mass;

    protected double inverseMass;
    protected Vector2d speed;
    protected double xMin;
    protected double xMax;
    protected double yMin;
    protected double yMax;
    protected boolean moved;
    public Body(double mass, double xMin, double xMax, double yMin, double yMax) {
        this.mass = mass;
        this.speed = new Vector2d(0.0, 0.0);
        this.inverseMass = 1 / mass;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public void move(double timeToMove) {
        xMin += speed.getX() * timeToMove;
        xMax += speed.getX() * timeToMove;
        yMin += speed.getY() * timeToMove;
        yMax += speed.getY() * timeToMove;
    }

    public double getMass() {
        return mass;
    }

    public double getXMin() {
        return xMin;
    }

    public double getXMax() {
        return xMax;
    }

    public double getYMin() {
        return yMin;
    }

    public double getYMax() {
        return yMax;
    }

}
