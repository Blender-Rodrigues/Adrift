package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Equipment;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.equipment.Jetpack;
import ee.taltech.iti0200.graphics.Model;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.Texture;
import ee.taltech.iti0200.graphics.ViewPort;
import org.joml.Matrix4f;

import java.io.IOException;


public class ToolbarRenderer implements Renderer {

    private static final int WEAPON_SIZE = 100;
    private static final int CONSUMABLE_SIZE = 50;

    private Texture manaBarEmpty;
    private Texture manaBarFull;

    private final Player player;
    private Model model;

    @Inject
    public ToolbarRenderer(@LocalPlayer Player player) {
        this.player = player;
    }

    @Override
    public void initialize() {
        model = Renderer.square;
        try {
            manaBarEmpty = new Texture("overhead/", "manaBarEmpty");
            manaBarFull = new Texture("overhead/", "manaBarFull");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // todo currently only renders properly, if only one consumable is rendered
    // sorry
    private void renderConsumables(Shader shader, ViewPort viewPort) {
        Matrix4f projection = viewPort.getStaticProjection(viewPort.getWidth() - 2 * CONSUMABLE_SIZE, viewPort.getHeight() - 2 * CONSUMABLE_SIZE, CONSUMABLE_SIZE, CONSUMABLE_SIZE);
        Matrix4f manaProjection = viewPort.getStaticProjection(viewPort.getWidth() - CONSUMABLE_SIZE / 2, viewPort.getHeight() - CONSUMABLE_SIZE / 2, CONSUMABLE_SIZE, CONSUMABLE_SIZE / 10f);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("rotation", new Matrix4f());

        for (Equipment consumable : player.getConsumables()) {
            shader.setUniform("projection", projection);
            projection.translate(-3f, 0, 0);
            manaProjection.translate(-3f, 0, 0);

            ((Drawable) consumable.getRenderer()).getTexture().bind(0);
            model.render();

            if (consumable instanceof Jetpack) {
                manaBarFull.bind(0);
                manaProjection.scale(getCharges(consumable), 1.0f, 1.0f);
                shader.setUniform("projection", manaProjection);
                model.render();
            }
        }
    }

    private float getCharges(Equipment jetpack) {
        return ((Jetpack) jetpack).getCharges() / (float) Jetpack.JACKPOT_CHARGES;
    }

}
