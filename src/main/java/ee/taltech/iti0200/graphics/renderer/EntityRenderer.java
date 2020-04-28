package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Transform;
import ee.taltech.iti0200.graphics.ViewPort;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class EntityRenderer implements Renderer {

    protected static transient Model model;
    protected transient Transform transform;
    protected Entity entity;

    public EntityRenderer setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public void initialize() {
        model = Renderer.square;

        transform = new Transform();
        transform.scale = new Vector3f((float) entity.getBoundingBox().getSize().getX(), (float) entity.getBoundingBox().getSize().getY(), 1);
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        Vector3f location = getLocation();
        transform.pos.set(location);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(viewPort.getProjection()));
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
    }

}
