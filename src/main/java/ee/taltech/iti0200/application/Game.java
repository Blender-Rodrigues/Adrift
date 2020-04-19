package ee.taltech.iti0200.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import ee.taltech.iti0200.di.ClientModule;
import ee.taltech.iti0200.di.GuiModule;
import ee.taltech.iti0200.di.ServerModule;
import ee.taltech.iti0200.di.SinglePlayerModule;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ee.taltech.iti0200.application.Component.priorities;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;

abstract public class Game {

    private final Logger logger = LogManager.getLogger(Game.class);

    protected EventBus eventBus;
    protected World world;
    protected List<Component> components = new LinkedList<>();

    private Timer timer;
    private long tick = 0;

    public Game(World world, EventBus eventBus, Timer timer) {
        this.world = world;
        this.timer = timer;
        this.eventBus = eventBus;
        Thread.currentThread().setName(getClass().getSimpleName());
    }

    public void run() {
        try {
            initialize();
            timer.initialize();

            components.sort(comparingInt(component -> priorities.getOrDefault(component.getClass(), 0)));

            for (Component component : components) {
                component.initialize();
            }
        } catch (Exception e) {
            logger.error("Initialization failed " + e.getMessage(), e);
            return;
        }

        logger.info(
            "Finished initialization with components: {}",
            components.stream()
                .map(Object::getClass)
                .map(Class::getSimpleName)
                .collect(joining(", "))
        );
        logger.info("Starting game loop.");

        try {
            while (isGameRunning()) {
                long start = System.currentTimeMillis();
                components.forEach(component -> component.update(tick));
                world.update(tick);
                loop(tick);
                tick = timer.sleep();
                System.out.println(System.currentTimeMillis() - start);
            }
        } finally {
            logger.warn("Terminating, game time: " + tick);
            terminate();
        }
    }

    protected abstract void loop(long tick);

    protected abstract void initialize() throws Exception;

    protected abstract boolean isGameRunning();

    private void terminate() {
        components.forEach(Component::terminate);
    }

    public static void main(String[] args) {
        List<Module> modules = new ArrayList<>();

        if (args.length == 0) {
            modules.add(new SinglePlayerModule());
            modules.add(new GuiModule());
        } else if (args[0].equalsIgnoreCase("server")) {
            int port = args.length >= 2 ? Integer.parseInt(args[1]) : 8880;

            modules.add(new ServerModule(port));
        } else if (args[0].equalsIgnoreCase("client")) {
            String host = args.length >= 2 ? args[1] : "localhost";
            int port = args.length >= 3 ? Integer.parseInt(args[2]) : 8880;
            String playerName = args.length >= 4 ? args[3] : null;

            modules.add(new ClientModule(host, port, playerName));
            modules.add(new GuiModule());
        } else {
            throw new IllegalArgumentException(
                "Invalid arguments provided, use one of the following: \n"
                    + "[no arguments] --> single player game\n"
                    + "server [port:8880] --> server game\n"
                    + "client [host:localhost] [port:8880] [playername: Unknown] --> client game\n"
            );
        }

        Injector injector = Guice.createInjector(modules);

        try {
            injector.getInstance(Game.class).run();
        } catch (RecreateException exception) {
            main(args);
        }
    }

}
