package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.graphics.*;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.BoundingBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.UUID;

import static ee.taltech.iti0200.graphics.Graphics.defaultTexture;

public class Entity extends Body {

    private static final long serialVersionUID = 1L;

    private UUID id = UUID.randomUUID();
    private boolean onFloor;
    public Renderer renderer;

    protected boolean movable = false;

    public Entity(double mass, BoundingBox boundingBox) {
        super(mass, boundingBox);
        renderer = new Drawable(this);
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

    public Renderer getRenderer() {
        return renderer;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + boundingBox.getCentre().rounded();
    }

}
