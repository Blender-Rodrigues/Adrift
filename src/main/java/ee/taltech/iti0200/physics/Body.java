package ee.taltech.iti0200.physics;

import java.io.Serializable;

public class Body implements Serializable {

    protected double mass;
    protected double inverseMass;
    protected Vector speed;
    protected BoundingBox boundingBox;
    protected boolean moved;
    protected double elasticity;
    protected double frictionCoefficient;
    protected double dragFromSurface;
    protected boolean collideable = false;
    protected double permeability;

    public Body(double mass, BoundingBox boundingBox) {
        this.mass = mass;
        this.speed = new Vector(0.0, 0.0);
        this.inverseMass = 1 / mass;
        this.boundingBox = boundingBox;
        this.elasticity = 1;
        this.frictionCoefficient = 1;
        this.permeability = 0;
    }

    public void move(double timeToMove) {
        Vector moveDelta = new Vector(this.speed);
        moveDelta.scale(timeToMove);
        this.move(moveDelta);
    }

    public void move(Vector moveDelta) {
        move(moveDelta, false);
    }

    public void move(Vector moveDelta, boolean testOnly) {
        this.boundingBox.move(moveDelta, testOnly);
    }

    public void accelerate(Vector accelerateDelta) {
        this.speed.add(accelerateDelta);
    }

    public void drag() {
        this.setXSpeed(this.dragFromSurface * this.frictionCoefficient * speed.getX());
    }

    public void airDrag(double resistance) {
        this.speed.scale(1 - resistance * this.permeability);
    }

    public double getMass() {
        return mass;
    }

    public Vector getSpeed() {
        return speed;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public boolean intersects(Body otherBody) {
        return this.boundingBox.intersects(otherBody.getBoundingBox());
    }

    public boolean isCollideable() {
        return collideable;
    }

    public void setXSpeed(double speed) {
        this.speed = new Vector(speed, this.speed.getY());
    }

    public void setYSpeed(double speed) {
        this.speed = new Vector(this.speed.getX(), speed);
    }

    public void setSpeed(Vector speed) {
        this.speed = new Vector(speed);
    }

    public void setPosition(Vector position) {
        this.boundingBox.getCentre().set(position);
    }

    public void setElasticity(double elasticity) {
        this.elasticity = elasticity;
    }

    public double getElasticity() {
        return this.elasticity;
    }

    public double getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public void setFrictionCoefficient(double frictionCoefficient) {
        this.frictionCoefficient = frictionCoefficient;
    }

    public double getDragFromSurface() {
        return dragFromSurface;
    }

    public void setDragFromSurface(double dragFromSurface) {
        this.dragFromSurface = dragFromSurface;
    }

}
