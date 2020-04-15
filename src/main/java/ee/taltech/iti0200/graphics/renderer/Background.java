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
    private Texture belt;

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
        belt = new Texture("world/", "belt");
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        transform.scale = new Vector3f(camera.getWidth() * camera.getZoom() / 2, camera.getHeight() * camera.getZoom() / 2, 1);

        shader.bind();
        shader.setUniform("sampler", 0);

        transform.pos.set(new Vector3f(-camera.getPosition().x, -camera.getPosition().y, 0));
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        backgroundImage.bind(0);
        model.render();

        transform.pos.set(new Vector3f(-camera.getPosition().x / 1.05f + 20, -camera.getPosition().y, 0));
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        belt.bind(0);
        model.render();
    }

}
