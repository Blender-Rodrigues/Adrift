package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.Transform;
import org.joml.Vector3f;

import java.io.IOException;

public class Background implements Renderer {

    private Model model;
    private Transform transform;

    private Texture backgroundImage;

    @Override
    public void initialize() throws IOException {
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

        backgroundImage = new Texture("world/", "background");
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        transform.scale = new Vector3f(30, 30, 1);
        Vector3f location = new Vector3f(-camera.getPosition().x, -camera.getPosition().y, 0);
        transform.pos.set(location);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        backgroundImage.bind(0);
        model.render();
    }

}
