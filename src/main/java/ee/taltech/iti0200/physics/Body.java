package ee.taltech.iti0200.physics;

import javax.vecmath.Vector2d;

public class Body {

    protected double mass;
    protected double inverseMass;
    protected Vector2d speed;
    protected AABB boundingBox;
    protected boolean moved;
    protected double elasticity;
    private boolean collideable;

    public Body(double mass, AABB boundingBox, boolean collideable) {
        this.mass = mass;
        this.speed = new Vector2d(0.0, 0.0);
        this.inverseMass = 1 / mass;
        this.boundingBox = boundingBox;
        this.collideable = collideable;
        this.elasticity = 1;
    }

    public Body(double mass, Vector2d min, Vector2d max, boolean collideable) {
        this(mass, new AABB(min, max), collideable);
    }

    public Body(
        double mass,
        Vector2d size,
        Vector2d position,
        boolean collideable,
        boolean usingPositionAndSize
    ) {
        this(mass, new AABB(position, size, usingPositionAndSize), collideable);
    }

    public void move(double timeToMove) {
        Vector2d moveDelta = new Vector2d(this.speed);
        moveDelta.scale(timeToMove);
        this.move(moveDelta);
    }

    public void move(Vector2d moveDelta) {
        this.boundingBox.move(moveDelta);
    }

    public void accelerate(Vector2d accelerateDelta) {
        this.speed.add(accelerateDelta);
    }

    public double getMass() {
        return mass;
    }

    public Vector2d getSpeed() {
        return speed;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public boolean intersects(Body otherBody) {
        return this.boundingBox.intersects(otherBody.getBoundingBox());
    }

    public boolean isCollideable() {
        return collideable;
    }

    public void setXSpeed(double speed) {
        this.speed = new Vector2d(speed, this.speed.getY());
    }

    public void setYSpeed(double speed) {
        this.speed = new Vector2d(this.speed.getX(), speed);
    }

    public void setElasticity(double elasticity) {
        this.elasticity = elasticity;
    }

    public double getElasticity() {
        return this.elasticity;
    }

}
