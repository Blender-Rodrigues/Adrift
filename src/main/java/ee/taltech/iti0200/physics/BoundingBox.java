package ee.taltech.iti0200.physics;

import static java.lang.Math.abs;

public class BoundingBox {

    protected Vector centre;
    protected Vector size;

    public BoundingBox(Vector min, Vector max) {
        this.size = new Vector(max);
        this.size.sub(min);
        this.size.scale(0.5);

        this.centre = new Vector(max);
        this.centre.add(min);
        this.centre.scale(0.5);
    }

    public BoundingBox(Vector position, Vector size, boolean usingPositionAndSize) {
        this.centre = position;
        this.size = size;
        this.size.scale(0.5);
    }

    public void move(Vector moveDelta) {
        this.centre.add(moveDelta);
    }

    public boolean intersects(BoundingBox otherBoundingBox) {
        Vector distance = new Vector(
                abs(this.centre.getX() - otherBoundingBox.getCentre().getX()),
                abs(this.centre.getY() - otherBoundingBox.getCentre().getY())
        );
        distance.sub(this.size);
        distance.sub(otherBoundingBox.getSize());
        return distance.getX() < 0 && distance.getY() < 0;
    }

    public Vector getOverLap(BoundingBox otherBoundingBox) {
        Vector distance = new Vector(
                abs(this.centre.getX() - otherBoundingBox.getCentre().getX()),
                abs(this.centre.getY() - otherBoundingBox.getCentre().getY())
        );
        distance.sub(this.size);
        distance.sub(otherBoundingBox.getSize());
        return distance;
    }

    public double getCentreXDirection(BoundingBox otherBoundingBox) {
        return (
            this.getCentre().getX()
            > otherBoundingBox.getCentre().getX()
        ) ? -1 : 1;
    }

    public double getCentreYDirection(BoundingBox otherBoundingBox) {
        return (
            this.getCentre().getY()
            > otherBoundingBox.getCentre().getY()
        ) ? -1 : 1;
    }

    public Vector getCentre() {
        return this.centre;
    }

    public Vector getSize() {
        return this.size;
    }

    public double getMinX() {
        return this.centre.getX() - this.size.getX();
    }

    public double getMinY() {
        return this.centre.getY() - this.size.getY();
    }

    public double getMaxX() {
        return this.centre.getX() + this.size.getX();
    }

    public double getMaxY() {
        return this.centre.getY() + this.size.getY();
    }

}
