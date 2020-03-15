package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.entity.Terrain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class BoundingBox implements Serializable {

    protected Vector centre;
    protected Vector size;

    public BoundingBox(double minX, double minY, double maxX, double maxY) {
        this.size = new Vector(maxX, maxY);
        this.size.x -= minX;
        this.size.y -= minY;
        this.size.scale(0.5);

        this.centre = new Vector(maxX, maxY);
        this.size.x += minX;
        this.size.y += minY;
        this.centre.scale(0.5);
    }

    public BoundingBox(Vector position, Vector size) {
        this.centre = new Vector(position);
        this.size = new Vector(size);
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

    public List<Double> getAllXCoordinates() {
        List<Double> xCoordinates = new ArrayList<>();
        int width = (int) (size.getX() * Terrain.TERRAIN_BLOCK_RESOLUTION * 2);
        double minY = clamp(getMinX());
        for (int i = 0; i < width; i++) {
            xCoordinates.add(minY + i / Terrain.TERRAIN_BLOCK_RESOLUTION);
        }
        return xCoordinates;
    }

    public static double clamp(double a) {
        return Math.round(a * Terrain.TERRAIN_BLOCK_RESOLUTION) / Terrain.TERRAIN_BLOCK_RESOLUTION;
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
