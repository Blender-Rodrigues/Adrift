package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Renderer {

    private Entity entity;
    protected transient Model model;
    private transient Transform transform;
    protected CoordinateConverter converter;

    public Renderer setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public Renderer initialize() {
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
        return this;
    }

    public void render(Shader shader, Camera camera, long tick, Matrix4f rotation) {
        Vector3f location = new Vector3f((float) entity.getBoundingBox().getCentre().x, (float) entity.getBoundingBox().getCentre().y, 0);
        transform.pos.set(location);

        Vector cameraVector = converter.physicsToCamera(entity.getBoundingBox().getCentre());
        cameraVector.setX(cameraVector.getX() * 2 / (camera.getZoom() * camera.getWidth()));
        cameraVector.setY(- cameraVector.getY() * 2 / (camera.getZoom() * camera.getHeight()));

        Matrix4f locationMatrix = new Matrix4f().setTranslation((float) cameraVector.getX(), (float) cameraVector.getY(), 0);
        Matrix4f inverseLocationMatrix = new Matrix4f().setTranslation((float) - cameraVector.getX(), (float) - cameraVector.getY(), 0);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        shader.setUniform("rotation", rotation);
        shader.setUniform("location", locationMatrix);
        shader.setUniform("inverseLocation", inverseLocationMatrix);
    }

}
