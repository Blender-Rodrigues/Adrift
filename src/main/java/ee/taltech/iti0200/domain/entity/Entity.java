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
    private transient Model model;
//    private transient Texture texture;
    private transient Animation texture;
    private transient Transform transform;

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

    public void initializeGraphics() {
        float[] vertices = new float[]{
            -1f, 1f, 0,
            1f, 1f, 0,
            1f, -1f, 0,
            -1f, -1f, 0
        };

        float[] texture = new float[]{
            0, 0,
            1, 0,
            1, 1,
            0, 1
        };

        int[] indices = new int[]{
            0, 1, 2,
            2, 3, 0
        };

        model = new Model(vertices, texture, indices);
//        this.texture = defaultTexture;
        this.texture = new Animation(6, "anim");

        transform = new Transform();
        transform.scale = new Vector3f((float) getBoundingBox().getSize().getX(), (float) getBoundingBox().getSize().getY(), 1);
    }

//    public void changeTexture(String filename) {
//        this.texture = new Texture(filename);
//    }

    public void render(Shader shader, Camera camera, long tick) {
        transform.pos.set(new Vector3f((float) this.boundingBox.getCentre().x, (float) this.boundingBox.getCentre().y, 0));

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        texture.bind(tick);
        model.render();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + boundingBox.getCentre().rounded();
    }

}
