package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.MainShader;
import ee.taltech.iti0200.di.annotations.WindowId;
import ee.taltech.iti0200.graphics.renderer.Background;
import ee.taltech.iti0200.graphics.renderer.EntityRenderFacade;
import ee.taltech.iti0200.graphics.renderer.GuiRenderFacade;

import java.io.IOException;

public class GameGraphics extends Graphics {

    private final EntityRenderFacade entityRenderer;
    private final GuiRenderFacade guiRenderer;
    private final Background backgroundRenderer;

    @Inject
    public GameGraphics(
        @WindowId long window,
        ViewPort viewPort,
        @MainShader Shader shader,
        EntityRenderFacade entityRenderer,
        GuiRenderFacade guiRenderer,
        Background backgroundRenderer
    ) {
        super(window, viewPort, shader);
        this.entityRenderer = entityRenderer;
        this.guiRenderer = guiRenderer;
        this.backgroundRenderer = backgroundRenderer;
    }

    @Override
    protected void initRenderers() throws IOException {
        backgroundRenderer.initialize();
        entityRenderer.initialize();
        guiRenderer.initialize();
    }

    @Override
    protected void updateRenderers(long tick) {
        backgroundRenderer.render(shader, viewPort, tick);
        entityRenderer.render(shader, viewPort, tick);
        guiRenderer.render(shader, viewPort, tick);
    }

}
