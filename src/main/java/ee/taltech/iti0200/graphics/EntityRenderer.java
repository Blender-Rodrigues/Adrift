package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class EntityRenderer implements Renderer {

    protected transient Model model;
    protected transient Transform transform;
    protected Entity entity;

    public EntityRenderer setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public void initialize() {
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

        transform = new Transform();
        transform.scale = new Vector3f((float) entity.getBoundingBox().getSize().getX(), (float) entity.getBoundingBox().getSize().getY(), 1);
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        Vector3f location = getLocation();
        transform.pos.set(location);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        setShaderRotation(shader);
    }

    // this is like a class variable except it is a method.
    protected Vector3f getLocation() {
        return new Vector3f((float) entity.getBoundingBox().getCentre().x, (float) entity.getBoundingBox().getCentre().y, 0);
    }

    /**
     * Does not rotate by default
     */
    protected void setShaderRotation(Shader shader) {
        shader.setUniform("rotation", new Matrix4f());
        shader.setUniform("location", new Matrix4f());
        shader.setUniform("inverseLocation", new Matrix4f());
    }

}
