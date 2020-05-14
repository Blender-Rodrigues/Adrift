package ee.taltech.iti0200.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ee.taltech.iti0200.menu.Menu;
import ee.taltech.iti0200.menu.MenuGraphics;
import ee.taltech.iti0200.menu.MenuInput;

public class MenuModule extends AbstractModule {

    private final Menu menu;

    public MenuModule(Menu menu) {
        this.menu = menu;
    }

    protected void configure() {
        bind(Menu.class).toInstance(menu);
        bind(MenuGraphics.class).in(Singleton.class);
        bind(MenuInput.class).in(Singleton.class);
    }

}
