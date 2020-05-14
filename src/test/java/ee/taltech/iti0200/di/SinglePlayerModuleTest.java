package ee.taltech.iti0200.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import ee.taltech.iti0200.application.Game;
import ee.taltech.iti0200.application.SinglePlayerGame;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.GuiTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SinglePlayerModuleTest {

    @GuiTest
    void configureCreatesBindings() {
        List<Module> modules = new ArrayList<>();
        modules.add(new GlfwModule());
        modules.add(new SinglePlayerModule());

        Injector injector = Guice.createInjector(modules);

        Player player = injector.getInstance(Key.get(Player.class, LocalPlayer.class));
        Game game = injector.getInstance(Game.class);

        assertThat(player.getName()).isEqualTo("Unknown");
        assertThat(game).isInstanceOf(SinglePlayerGame.class);
    }

}
