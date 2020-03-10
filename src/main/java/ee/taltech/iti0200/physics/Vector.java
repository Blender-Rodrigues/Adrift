package ee.taltech.iti0200.physics;

import javax.vecmath.Vector2d;

public class Vector extends Vector2d {

    public Vector(double x, double y) {
        super(x, y);
    }

    public Vector(Vector vector) {
        super(vector);
    }

    public Vector() {
        super();
    }

    public void elementWiseDivide(Vector2d divisor) {
        double newX = (divisor.getX() == 0) ? 0 : this.getX() / divisor.getX();
        double newY = (divisor.getY() == 0) ? 0 : this.getY() / divisor.getY();
        this.setX(newX);
        this.setY(newY);
    }

    public void elementWiseMultiple(Vector2d vector) {
        this.setX(this.getX() * vector.getX());
        this.setY(this.getY() * vector.getY());
    }

    public Vector rounded() {
        int xInt = (int) getX() * 100;
        int yInt = (int) getY() * 100;
        double x = (double) xInt / 100d;
        double y = (double) yInt / 100d;
        return new Vector(x, y);
    }

}
