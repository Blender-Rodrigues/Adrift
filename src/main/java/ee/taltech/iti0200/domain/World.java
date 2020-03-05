package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Vector;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
        terrainMap = imMovableBodies.stream()
            .filter(entity -> entity instanceof Terrain)
            .flatMap(entity -> {
                double maxY = entity.getBoundingBox().getMaxY();
                return entity.getBoundingBox().getAllXCoordinates().stream()
                    .map(pos -> new SimpleEntry<>(new Vector(pos, maxY), (Terrain) entity));
            })
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    public Map<Vector, Terrain> getTerrainMap() {
        return terrainMap;
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
