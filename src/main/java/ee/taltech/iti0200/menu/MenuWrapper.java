package ee.taltech.iti0200.menu;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import ee.taltech.iti0200.application.Game;
import ee.taltech.iti0200.application.RestartGame;
import ee.taltech.iti0200.di.ClientModule;
import ee.taltech.iti0200.di.GlfwModule;
import ee.taltech.iti0200.di.GuiModule;
import ee.taltech.iti0200.di.MenuModule;
import ee.taltech.iti0200.di.ServerModule;
import ee.taltech.iti0200.di.SinglePlayerModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MenuWrapper {

    private static final Logger logger = LogManager.getLogger(MenuWrapper.class);

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            runServer(args);
            return;
        }

        GlfwModule glfw = new GlfwModule();
        Menu menu = new Menu(args).setMessage("Welcome, pick a game mode");

        try {
            runMenu(menu, glfw);
        } catch (RestartGame exception) {
            logger.info("Exited from game: " + exception.getMessage());
        } catch (Exception exception) {
            logger.error("Game closed with: " + exception.getMessage(), exception);
        } finally {
            glfw.terminate();
        }
    }

    private static void runMenu(Menu menu, GlfwModule glfw) throws InterruptedException, IOException {
        Injector injector = Guice.createInjector(asList(glfw, new MenuModule(menu)));

        MenuGraphics graphics = injector.getInstance(MenuGraphics.class);
        MenuInput input = injector.getInstance(MenuInput.class);

        graphics.initialize();
        input.initialize();

        menu.setGameMode(0);
        long tick = 0;

        while (!menu.isGameModeSelected()) {
            graphics.update(tick);
            input.update(tick);

            if (!graphics.isWindowOpen()) {
                return;
            }

            Thread.sleep(10);
            tick++;
        }

        menu.setMessage("Starting...");
        graphics.update(++tick);

        input.terminate();
        graphics.terminate();

        runClient(menu, glfw);
    }

    private static void runClient(Menu menu, GlfwModule glfw) throws InterruptedException, IOException {
        List<Module> modules = new ArrayList<>();
        modules.add(glfw);

        try {
            if (menu.getGameMode() == 1) {
                modules.add(new SinglePlayerModule());
            } else {
                modules.add(new ClientModule(
                    menu.getHost().getValue(),
                    Integer.parseInt(menu.getPort().getValue()),
                    menu.getPlayerName().getValue()
                ));
            }

            modules.add(new GuiModule());

            Guice.createInjector(modules).getInstance(Game.class).run();

            menu.setMessage("Game initialization failed");
        } catch (RestartGame exception) {
            menu.setMessage(exception.getMessage());
        } catch (Exception exception) {
            logger.error("Game closed with: " + exception.getMessage(), exception);
            menu.setMessage(exception.getMessage());
        } finally {
            runMenu(menu, glfw);
        }
    }

    private static void runServer(String[] args) {
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : 8880;
        Injector injector = Guice.createInjector(new ServerModule(port));

        try {
            injector.getInstance(Game.class).run();
        } catch (RestartGame exception) {
            logger.info(exception);
            runServer(args);
        }
    }

}
