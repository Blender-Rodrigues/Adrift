package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Vector;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.taltech.iti0200.graphics.Graphics.defaultTexture;

public class World {

    protected List<Entity> entities = new ArrayList<>();
    protected List<Living> livingEntities = new ArrayList<>();
    protected List<Entity> movableBodies = new ArrayList<>();
    protected List<Entity> imMovableBodies = new ArrayList<>();
    protected Map<Vector, Terrain> terrainMap;
    protected double xMin;
    protected double xMax;
    protected double yMin;
    protected double yMax;
    protected long time;
    protected double timeStep;

    public World(double xMin, double xMax, double yMin, double yMax, double timeStep) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.timeStep = timeStep;
    }

    public void initialize() {
        addBody(new Bot(new Vector(10.0, 4.0), this), true);
        addBody(new Bot(new Vector(30.0, 4.0), this), true);
        for (int i = 0; i < 20; i++) {
            addBody(new Terrain(new Vector(i * 2.0 + 1.0, 1.0)), false);
        }
        addBody(new Terrain(new Vector(1.0, 3.0)), false);
        addBody(new Terrain(new Vector(39.0, 3.0)), false);
        mapTerrain();
    }

    public void update(long tick) {
        time = tick;
        livingEntities.forEach(entity -> entity.update(tick));
        entities.removeIf(Entity::isRemoved);
        livingEntities.removeIf(Entity::isRemoved);
        movableBodies.removeIf(Entity::isRemoved);
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

    public long getTime() {
        return time;
    }

    public Map<Vector, Terrain> getTerrainMap() {
        return terrainMap;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void addBody(Entity body, boolean movable) {
        entities.add(body);
        if (defaultTexture != null) {
            body.initializeGraphics();
        }
        if (movable) {
            movableBodies.add(body);
        } else {
            imMovableBodies.add(body);
        }
        if (body instanceof Living) {
            livingEntities.add((Living) body);
        }
    }

    public List<Entity> getMovableBodies() {
        return movableBodies;
    }

    public List<Entity> getImMovableBodies() {
        return imMovableBodies;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Living> getLivingEntities() {
        return livingEntities;
    }

    public List<Projectile> getProjectiles() {
        return movableBodies.stream()
            .filter(body -> body instanceof Projectile)
            .map(entity -> (Projectile) entity)
            .collect(Collectors.toList());
    }

}
