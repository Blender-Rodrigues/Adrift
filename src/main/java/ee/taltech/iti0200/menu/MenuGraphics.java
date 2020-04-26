package ee.taltech.iti0200.menu;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.WindowId;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.graphics.renderer.Background;

import java.io.IOException;

public class MenuGraphics extends Graphics {

    private final Background background;

    @Inject
    public MenuGraphics(@WindowId long window, ViewPort viewPort, Background background) {
        super(window, viewPort);
        this.background = background;
    }

    @Override
    protected void initRenderers() throws IOException {
        background.initialize();
    }

    @Override
    protected void updateRenderers(long tick) {
        background.render(shader, viewPort, tick);
    }

}
