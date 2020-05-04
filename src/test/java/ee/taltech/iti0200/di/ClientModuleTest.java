package ee.taltech.iti0200.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import ee.taltech.iti0200.application.ClientGame;
import ee.taltech.iti0200.application.Game;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.di.annotations.ServerHost;
import ee.taltech.iti0200.di.annotations.ServerTcpPort;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.GuiTest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClientModuleTest {

    @GuiTest
    void configureCreatesBindings() {
        List<Module> modules = new ArrayList<>();
        modules.add(new GlfwModule());
        modules.add(new ClientModule("localhost", 6666, "test player"));

        Injector injector = Guice.createInjector(modules);

        Integer port = injector.getInstance(Key.get(Integer.class, ServerTcpPort.class));
        InetAddress address = injector.getInstance(Key.get(InetAddress.class, ServerHost.class));
        Player player = injector.getInstance(Key.get(Player.class, LocalPlayer.class));
        Game game = injector.getInstance(Game.class);

        assertThat(port).isEqualTo(6666);
        assertThat(address.getHostName()).isEqualTo("localhost");
        assertThat(player.getName()).isEqualTo("test player");
        assertThat(game).isInstanceOf(ClientGame.class);
    }

}
