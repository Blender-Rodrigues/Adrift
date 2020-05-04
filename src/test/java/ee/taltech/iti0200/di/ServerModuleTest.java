package ee.taltech.iti0200.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import ee.taltech.iti0200.application.Game;
import ee.taltech.iti0200.application.ServerGame;
import org.junit.jupiter.api.Test;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServerModuleTest {

    @Test
    void configureCreatesBindings() {
        List<Module> modules = new ArrayList<>();
        modules.add(new ServerModule(6666));

        Injector injector = Guice.createInjector(modules);

        ServerSocket socket = injector.getInstance(ServerSocket.class);
        Game game = injector.getInstance(Game.class);

        assertThat(socket.getLocalPort()).isEqualTo(6666);
        assertThat(game).isInstanceOf(ServerGame.class);
    }

}
