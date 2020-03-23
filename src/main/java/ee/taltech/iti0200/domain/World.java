package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.physics.Vector;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static ee.taltech.iti0200.graphics.Graphics.defaultTexture;

public class World {

    protected HashMap<UUID, Entity> entities = new HashMap<>();
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
        for (int i = 0; i < 20; i++) {
            addEntity(new Terrain(new Vector(i * 2.0 + 1.0, 1.0)));
        }
        addEntity(new Terrain(new Vector(1.0, 3.0)));
        addEntity(new Terrain(new Vector(39.0, 3.0)));
        mapTerrain();
    }

    public void update(long tick) {
        time = tick;
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

    public Vector nextPlayerSpawnPoint() {
        return new Vector(20.0, 40.0);
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

    public List<Entity> getMovableBodies() {
        return movableBodies;
    }

    public List<Entity> getImMovableBodies() {
        return imMovableBodies;
    }

    public Collection<Entity> getEntities() {
        return entities.values();
    }

    public List<Living> getLivingEntities() {
        return livingEntities;
    }

    public List<Projectile> getProjectiles() {
        return movableBodies.stream()
            .filter(Projectile.class::isInstance)
            .map(entity -> (Projectile) entity)
            .collect(Collectors.toList());
    }

    public Entity getEntity(UUID id) {
        return entities.get(id);
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
        if (defaultTexture != null) {
            entity.initializeGraphics();
        }
        if (entity.isMovable()) {
            movableBodies.add(entity);
        } else {
            imMovableBodies.add(entity);
        }
        if (entity instanceof Living) {
            ((Living) entity).setWorld(this);
            livingEntities.add((Living) entity);
        }
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity.getId());

        if (entity instanceof Living) {
            livingEntities.remove(entity);
            ((Living) entity).setAlive(false);
        }

        if (entity.isMovable()) {
            movableBodies.remove(entity);
        } else {
            imMovableBodies.remove(entity);
        }

        if (entity instanceof Terrain) {
            entity.getBoundingBox()
                .getAllXCoordinates()
                .stream()
                .map(x -> new Vector(x, entity.getBoundingBox().getMaxY()))
                .forEach(vector -> terrainMap.remove(vector));
        }
    }

}
