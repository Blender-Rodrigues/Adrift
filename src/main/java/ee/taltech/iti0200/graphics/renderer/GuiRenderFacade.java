package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.di.factory.RendererFactory;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Shader;


import java.io.IOException;

public class GuiRenderFacade implements Renderer {

    private Alphabet alphabet;
    private ToolbarRenderer toolbar;
    private ScoreRenderer scoreRenderer;
    private RendererFactory rendererFactory;
    private Player player;

    @Inject
    public GuiRenderFacade(@LocalPlayer Player player, RendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;
        this.player = player;
    }

    @Override
    public void initialize() throws IOException {
        alphabet = new Alphabet("fonts/", "basicFont");
        alphabet.initialize();
        scoreRenderer = rendererFactory.createScoreRenderer();

        toolbar = new ToolbarRenderer(player);
        toolbar.initialize();
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        scoreRenderer.render(alphabet, camera);
        toolbar.render(shader, camera, tick);
    }

}
