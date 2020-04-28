package ee.taltech.iti0200.menu;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.MainShader;
import ee.taltech.iti0200.di.annotations.WindowId;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;

import java.io.IOException;

public class MenuGraphics extends Graphics {

    private final MenuRenderer MenuRenderer;

    @Inject
    public MenuGraphics(@WindowId long window, ViewPort viewPort, @MainShader Shader shader, MenuRenderer MenuRenderer) {
        super(window, viewPort, shader);
        this.MenuRenderer = MenuRenderer;
    }

    @Override
    protected void initRenderers() throws IOException {
        MenuRenderer.initialize();
    }

    @Override
    protected void updateRenderers(long tick) {
        MenuRenderer.render(shader, viewPort, tick);
    }

}
