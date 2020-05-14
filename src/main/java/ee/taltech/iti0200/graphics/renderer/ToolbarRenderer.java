package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Equipment;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;
import org.joml.Matrix4f;


public class ToolbarRenderer implements Renderer {

    private static final int WEAPON_SIZE = 100;
    private static final int CONSUMABLE_SIZE = 50;

    private final Player player;
    private Model model;

    @Inject
    public ToolbarRenderer(@LocalPlayer Player player) {
        this.player = player;
    }

    @Override
    public void initialize() {
        model = Renderer.square;
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        renderWeapons(shader, viewPort);
        renderConsumables(shader, viewPort);
    }

    private void renderWeapons(Shader shader, ViewPort viewPort) {
        Matrix4f projection = viewPort.getStaticProjection(viewPort.getWidth() / 2 - WEAPON_SIZE / 2, viewPort.getHeight() - WEAPON_SIZE, WEAPON_SIZE, WEAPON_SIZE);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("rotation", new Matrix4f());

        projection.translate(-0.75f * player.getWeapons().size(), 0, 0);

        for (Gun weapon : player.getWeapons()) {
            float offset = weapon.isActive() ? 0.5f : 0;

            projection.translate(0, offset, 0);
            shader.setUniform("projection", projection);
            projection.translate(1.5f, -offset, 0);

            ((Drawable) weapon.getRenderer()).getTexture().bind(0);
            model.render();
        }
    }

    private void renderConsumables(Shader shader, ViewPort viewPort) {
        Matrix4f projection = viewPort.getStaticProjection(viewPort.getWidth() - 2 * CONSUMABLE_SIZE, viewPort.getHeight() - 2 * CONSUMABLE_SIZE, CONSUMABLE_SIZE, CONSUMABLE_SIZE);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("rotation", new Matrix4f());

        for (Equipment consumable: player.getConsumables()) {
            shader.setUniform("projection", projection);
            projection.translate(-3f, 0, 0);

            ((Drawable) consumable.getRenderer()).getTexture().bind(0);
            model.render();
        }
    }

}
