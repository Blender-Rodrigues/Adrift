package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.domain.entity.Equipment;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import org.joml.Matrix4f;


public class ToolbarRenderer implements Renderer {

    public static final int SIZE = 100;
    private Model model;
    private Player player;

    public ToolbarRenderer(Player player) {
        this.player = player;
    }

    @Override
    public void initialize() {
        model = createSquare();
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        Matrix4f projection = camera.getStaticProjection(camera.getWidth() / 2 - SIZE / 2, camera.getHeight() - SIZE, SIZE, SIZE);

        shader.bind();
        shader.setUniform("sampler", 0);

        float offset = -player.getWeapons().size();
        projection.translate(0.75f * offset, 0, 0);
        for (Equipment equipment : player.getEquipment()) {
            shader.setUniform("projection", projection);
            ((Drawable) equipment.getRenderer()).getTexture().bind(0);
            model.render();
            projection.translate(1.5f, 0, 0);
        }
    }

}
