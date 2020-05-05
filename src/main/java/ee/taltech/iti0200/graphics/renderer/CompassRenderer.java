package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Matrix4f;

import java.io.IOException;

public class CompassRenderer implements Renderer {

    private final World world;
    private Model model;

    private Vector direction;
    private Texture texture;

    @Inject
    public CompassRenderer(World world) {
        this.world = world;
    }

    @Override
    public void initialize() throws IOException {
        model = Renderer.square;
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        Matrix4f projection = new Matrix4f();
        projection.translate((float) direction.getX(), (float) direction.getY(), 0);
        projection.scale(0.1f);

        Matrix4f rotation = new Matrix4f();
        float rotationAngle = - (float) (Math.atan2(direction.getX(), direction.getY()));
        rotation.setRotationXYZ(0, 0, rotationAngle + (float) Math.PI / 2);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", projection);
        shader.setUniform("rotation", rotation);

        texture.bind(0);
        model.render();
        shader.setUniform("rotation", new Matrix4f());
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
        this.direction.normalize();
        direction.scale(0.9);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
