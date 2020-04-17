package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Transform;
import org.joml.Vector3f;

import java.io.IOException;


public class ToolbarRenderer implements Renderer {

    private Model model;
    private Transform transform;
    private Player player;

    public ToolbarRenderer(Player player) {
        this.player = player;
    }

    @Override
    public void initialize() throws IOException {
        model = createSquare();
        transform = new Transform();
        transform.scale = new Vector3f(1f, 1f, 1);
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        transform.scale = new Vector3f(50f * camera.getZoom(), 50f * camera.getZoom(), 1);
        transform.pos = new Vector3f(-camera.getPosition().x, -camera.getPosition().y - camera.getHeight() * camera.getZoom() / 2.2f, 0);

        shader.bind();
        shader.setUniform("sampler", 0);

        float offset = (-1f * (player.getWeapons().size() - 1));
        transform.pos.add(offset, 0, 0);
        for (Gun weapon : player.getWeapons()) {
            shader.setUniform("projection", transform.getProjection(camera.getProjection()));
            ((RotatingDrawable) weapon.getRenderer()).getTexture().bind(0);
            model.render();
            transform.pos.add(2f, 0, 0);
        }
    }

}
