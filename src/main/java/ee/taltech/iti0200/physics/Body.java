package ee.taltech.iti0200.physics;

public class Body {

    protected double mass;

    protected double inverseMass;
    protected double xSpeed;
    protected double ySpeed;
    protected double xMin;
    protected double xMax;
    protected double yMin;
    protected double yMax;
    protected boolean moved;
    public Body(double mass, double xMin, double xMax, double yMin, double yMax) {
        this.mass = mass;
        this.xSpeed = 0.0;
        this.ySpeed = 0.0;
        this.inverseMass = 1 / mass;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public void move(double timeToMove) {
        xMin += xSpeed * timeToMove;
        xMax += xSpeed * timeToMove;
        yMin += ySpeed * timeToMove;
        yMax += ySpeed * timeToMove;
    }

    public double getMass() {
        return mass;
    }

    public double getxMin() {
        return xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public double getyMax() {
        return yMax;
    }

}
