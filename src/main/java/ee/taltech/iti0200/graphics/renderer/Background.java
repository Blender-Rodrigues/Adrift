package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.Transform;
import ee.taltech.iti0200.graphics.ViewPort;
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
    public void render(Shader shader, ViewPort viewPort, long tick) {
        float[] dimensions = backgroundImage.scaleToViewPort(viewPort);
        transform.scale = new Vector3f(dimensions[0] * viewPort.getZoom() / 2, dimensions[1] * viewPort.getZoom() / 2, 1);

        shader.bind();
        shader.setUniform("sampler", 0);

        transform.pos.set(new Vector3f(-viewPort.getPosition().x, -viewPort.getPosition().y, 0));
        shader.setUniform("projection", transform.getProjection(viewPort.getProjection()));
        backgroundImage.bind(0);
        model.render();

        transform.pos.set(new Vector3f(-viewPort.getPosition().x / 1.05f + 20, -viewPort.getPosition().y, 0));
        shader.setUniform("projection", transform.getProjection(viewPort.getProjection()));
        belt.bind(0);
        model.render();
    }

}
