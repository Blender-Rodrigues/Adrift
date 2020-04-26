package ee.taltech.iti0200.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.GameGraphics;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.graphics.renderer.EntityRenderFacade;
import ee.taltech.iti0200.input.GameInput;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.input.Mouse;

public class GuiModule extends AbstractModule {

    protected void configure() {
        bind(Camera.class).in(Singleton.class);
        bind(ViewPort.class).to(Camera.class).in(Singleton.class);
        bind(Input.class).to(GameInput.class).in(Singleton.class);
        bind(Graphics.class).to(GameGraphics.class).in(Singleton.class);
        bind(Mouse.class).in(Singleton.class);
        bind(EntityRenderFacade.class).in(Singleton.class);
    }

}
