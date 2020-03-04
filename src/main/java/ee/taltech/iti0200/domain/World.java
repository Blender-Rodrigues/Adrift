package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    protected List<Entity> movableBodies = new ArrayList<>();
    protected List<Entity> imMovableBodies = new ArrayList<>();
    protected Map<Vector, Terrain> terrainMap;
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

    public void initialize() {
        addBody(new Bot(new Vector(10.0, 4.0)), true);
        addBody(new Bot(new Vector(30.0, 4.0)), true);
        for (int i = 0; i < 20; i++) {
            addBody(new Terrain(new Vector(i * 2.0 + 1.0, 1.0)), false);
        }
        addBody(new Terrain(new Vector(1.0, 3.0)), false);
        addBody(new Terrain(new Vector(39.0, 3.0)), false);
        mapTerrain();
    }

    public void mapTerrain() {
        terrainMap = new HashMap<>();
        for (Entity entity: imMovableBodies) {
            if (entity.getClass() != Terrain.class) {
                continue;
            }
            for (int i = 0; i < ((Terrain) entity).getIntegerWidth(); i++) {
                Vector leftCoordinate = new Vector(
                    entity.getBoundingBox().getCentre().getX() - i * 0.01,
                    entity.getBoundingBox().getCentre().getY() + entity.getBoundingBox().getSize().getY()
                );
                Vector rightCoordinate = new Vector(
                    entity.getBoundingBox().getCentre().getX() + i * 0.01,
                    entity.getBoundingBox().getCentre().getY() + entity.getBoundingBox().getSize().getY()
                );
                terrainMap.put(leftCoordinate, (Terrain) entity);
                terrainMap.put(rightCoordinate, (Terrain) entity);
            }
        }
    }

    public double getTimeStep() {
        return timeStep;
    }

    private void addMovableBody(Entity body) {
        movableBodies.add(body);
    }

    private void addImMovableBody(Entity body) {
        imMovableBodies.add(body);
    }

    public void addBody(Entity body, boolean movable) {
        if (movable) {
            addMovableBody(body);
        } else {
            addImMovableBody(body);
        }
    }

    public List<Entity> getMovableBodies() {
        return movableBodies;
    }

    public List<Entity> getImMovableBodies() {
        return imMovableBodies;
    }

}
