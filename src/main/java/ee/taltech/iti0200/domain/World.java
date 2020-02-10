package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Body;

import java.util.ArrayList;
import java.util.List;

public class World {

    protected List<Body> movableBodies = new ArrayList<>();
    protected List<Body> imMovableBodies = new ArrayList<>();
    protected double xMin;
    protected double xMax;
    protected double yMin;
    protected double yMax;
    protected double timeStep;

    public World(double xMin, double xMax, double yMin, double yMax, double timeStep) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.timeStep = timeStep;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void moveBodies() {
        for (Body body: movableBodies) {
            body.move(timeStep);
        }
    }

    private void addMovableBody(Body body) {
        movableBodies.add(body);
    }

    private void addImMovableBody(Body body) {
        imMovableBodies.add(body);
    }

    public void addBody(Body body, boolean movable) {
        if (movable) {
            addMovableBody(body);
        } else {
            addImMovableBody(body);
        }
    }

}
