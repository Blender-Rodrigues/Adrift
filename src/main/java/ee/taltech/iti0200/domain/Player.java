package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.graphics.*;
import ee.taltech.iti0200.physics.Body;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.vecmath.Vector2d;
import java.util.Arrays;

public class Player extends Entity {

    private static final Vector2d size = new Vector2d(1.5, 1.5);
    private static final double mass = 70.0;

    private Model model;
    private Texture texture;
    private Transform transform;

    public Player(Vector2d position) {
        super(Arrays.asList(new Body(mass, size, position, true, true)), false);

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

    public void moveRight() {
//        transform.pos.add(new Vector3f(0.2f, 0, 0));
        transform.pos.set(new Vector3f((float)this.boundingBox.getCentre().x, (float)this.boundingBox.getCentre().y, 0));
//        this.boundingBox.getCentre();
    }

    public void render(Shader shader, Camera camera) {
        transform.pos.set(new Vector3f((float)this.boundingBox.getCentre().x/10, (float)this.boundingBox.getCentre().y/10, 0));

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        texture.bind(0);
        model.render();
    }
}
