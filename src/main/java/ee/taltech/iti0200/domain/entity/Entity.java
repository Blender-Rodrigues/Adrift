package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Renderer;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.BoundingBox;

import java.util.HashMap;
import java.util.UUID;

import static ee.taltech.iti0200.graphics.Graphics.DEFAULT;

public class Entity extends Body {

    private static final long serialVersionUID = 1L;

    private UUID id = UUID.randomUUID();
    private boolean onFloor;
    public HashMap<String, Renderer> renderers = new HashMap<>();

    protected boolean movable = false;

    public Entity(double mass, BoundingBox boundingBox) {
        super(mass, boundingBox);
    }

    public boolean isMovable() {
        return movable;
    }

    public boolean isOnFloor() {
        return onFloor;
    }

    public void setOnFloor(boolean onFloor) {
        this.onFloor = onFloor;
    }

    public UUID getId() {
        return id;
    }

    public Entity setId(UUID id) {
        this.id = id;
        return this;
    }

    public Entity setRenderers(HashMap<String, Renderer> renderers) {
        this.renderers = renderers;
        return this;
    }

    public void render(Shader shader, Camera camera, long tick) {
        renderers.get(DEFAULT).render(shader, camera, tick);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + boundingBox.getCentre().rounded();
    }

}
