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
        model = createSquare();
        transform = new Transform();

        backgroundImage = new Texture("world/", "background");
        belt = new Texture("world/", "belt");
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        float[] dimensions = getDimensions(camera);
        transform.scale = new Vector3f(dimensions[0] * camera.getZoom() / 2, dimensions[1] * camera.getZoom() / 2, 1);

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

    private float[] getDimensions(Camera camera) {
        float cameraWidth = camera.getWidth();
        float cameraHeight = camera.getHeight();
        float bgWidth = backgroundImage.getWidth();
        float bgHeight = backgroundImage.getHeight();

        float cameraRatio = cameraWidth / cameraHeight;
        float backgroundRatio = bgWidth / bgHeight;

        if (cameraRatio >= backgroundRatio) {
            return new float[] {cameraWidth, cameraWidth / backgroundRatio};
        } else {
            return new float[] {cameraHeight * backgroundRatio, cameraHeight};
        }
    }

}
