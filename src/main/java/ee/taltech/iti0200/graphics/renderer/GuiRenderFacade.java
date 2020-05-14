package ee.taltech.iti0200.graphics.renderer;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.factory.RendererFactory;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;

public class GuiRenderFacade implements Renderer {

    private final RendererFactory rendererFactory;
    private final ToolbarRenderer toolbar;

    private ScoreRenderer scoreRenderer;
    private Alphabet alphabet;

    @Inject
    public GuiRenderFacade(RendererFactory rendererFactory, ToolbarRenderer toolbar, Alphabet alphabet) {
        this.rendererFactory = rendererFactory;
        this.toolbar = toolbar;
        this.alphabet = alphabet;
    }

    @Override
    public void initialize() {
        alphabet.initialize();
        scoreRenderer = rendererFactory.createScoreRenderer();
        toolbar.initialize();
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        scoreRenderer.render(alphabet, viewPort);
        toolbar.render(shader, viewPort, tick);
    }

}
