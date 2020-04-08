package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.factory.RendererFactory;

import java.io.IOException;

public class GuiRenderFacade implements Renderer {

    private Alphabet alphabet;
    private ScoreRenderer scoreRenderer;
    private RendererFactory rendererFactory;

    @Inject
    public GuiRenderFacade(RendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;
    }

    @Override
    public void initialize() throws IOException {
        alphabet = new Alphabet("fonts/", "basicFont");
        alphabet.initialize();

        scoreRenderer = rendererFactory.createScoreRenderer();
    }

    @Override
    public void render(Shader shader, Camera camera, long tick) {
        scoreRenderer.render(alphabet, camera);
    }


}
