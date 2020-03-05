package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.Transform;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Vector3f;

public class Player extends Entity {

    private static final Vector size = new Vector(1.5, 1.5);
    private static final double mass = 70.0;
    private static final double elasticity = 0.25;

    private Model model;
    private Texture texture;
    private Transform transform;

    public Player(Vector position) {
        super(new Body(mass, new Vector(size), position, true, true), false);
        setElasticity(elasticity);
    }

    public void initialize() {
        float[] vertices = new float[] {
            -1f, 1f, 0,
            1f, 1f, 0,
            1f, -1f, 0,
            -1f, -1f, 0
        };

        float[] texture = new float[] {
            0, 0,
            1, 0,
            1, 1,
            0, 1
        };

        int[] indices = new int[] {
            0, 1, 2,
            2, 3, 0
        };

        model = new Model(vertices, texture, indices);
        this.texture = new Texture("smile.png");

        transform = new Transform();
        transform.scale = new Vector3f(32, 32, 1);
    }

    public void render(Shader shader, Camera camera) {
        // TODO: move magic number 10 to constant
        transform.pos.set(new Vector3f((float)this.boundingBox.getCentre().x / 10, (float)this.boundingBox.getCentre().y / 10, 0));

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        texture.bind(0);
        model.render();
    }

}
