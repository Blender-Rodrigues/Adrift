package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Equipment;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;
import org.joml.Matrix4f;


public class ToolbarRenderer implements Renderer {

    private static final int SIZE = 100;

    private final Player player;
    private Model model;

    @Inject
    public ToolbarRenderer(@LocalPlayer Player player) {
        this.player = player;
    }

    @Override
    public void initialize() {
        model = createSquare();
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        Matrix4f projection = viewPort.getStaticProjection(viewPort.getWidth() / 2 - SIZE / 2, viewPort.getHeight() - SIZE, SIZE, SIZE);

        shader.bind();
        shader.setUniform("sampler", 0);

        projection.translate(-0.75f * player.getEquipment().size(), 0, 0);

        for (Equipment equipment : player.getEquipment()) {
            float offset = equipment.isActive() ? 0.5f : 0;

            projection.translate(0, offset, 0);
            shader.setUniform("projection", projection);
            projection.translate(1.5f, -offset, 0);

            ((Drawable) equipment.getRenderer()).getTexture().bind(0);
            model.render();
        }
    }

}
