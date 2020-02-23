package ee.taltech.iti0200.physics;

import javax.vecmath.Vector2d;

import static java.lang.Math.abs;

public class AABB {
    protected Vector2d centre;
    protected Vector2d size;

    public AABB(Vector2d min, Vector2d max) {
        this.size = new Vector2d(max);
        this.size.sub(min);
        this.size.scale(0.5);

        this.centre = new Vector2d(max);
        this.centre.add(min);
        this.centre.scale(0.5);
    }

    public AABB(Vector2d position, Vector2d size, boolean usingPositionAndSize) {
        this.centre = position;
        this.size = size;
        this.size.scale(0.5);
    }

    public void move(Vector2d moveDelta) {
        this.centre.add(moveDelta);
    }

    public boolean intersects(AABB otherBoundingBox) {
        Vector2d distance = new Vector2d(
                abs(this.centre.getX() - otherBoundingBox.getCentre().getX()),
                abs(this.centre.getY() - otherBoundingBox.getCentre().getY())
        );
        distance.sub(this.size);
        distance.sub(otherBoundingBox.size);
        return distance.getX() < 0 || distance.getY() < 0;
    }

    public Vector2d getOverLap(AABB otherBoundingBox) {
        Vector2d distance = new Vector2d(
                abs(this.centre.getX() - otherBoundingBox.getCentre().getX()),
                abs(this.centre.getY() - otherBoundingBox.getCentre().getY())
        );
        distance.sub(this.size);
        distance.sub(otherBoundingBox.size);
        return distance;
    }

    public Vector2d getCentre() {
        return this.centre;
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
