package ee.taltech.iti0200.application;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.Physics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

import static ee.taltech.iti0200.application.Component.priorities;
import static java.util.Comparator.comparingInt;

abstract public class Game {

    protected World world;
    protected List<Component> components = new LinkedList<>();

    private Timer timer;
    private long tick = 0;
    private Logger logger;

    public Game() {
        Thread.currentThread().setName(getClass().getSimpleName());
        world = new World(0.0, 40.0, 0.0, 40.0, 0.05);
        timer = new Timer(20F);
        components.add(new Physics(world));
        logger = LogManager.getLogger(Game.class);
    }

    void run() {
        try {
            world.initialize();
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

        logger.info("Finished initialization. Starting game loop.");

        while (isGameRunning()) {
            components.forEach(component -> component.update(tick));
            loop(tick);
            tick = timer.sleep();
        }

        logger.info("Terminating, game time: " + tick);

        terminate();
    }

    protected abstract void loop(long tick);

    protected abstract void initialize() throws Exception;

    protected abstract boolean isGameRunning();

    private void terminate() {
        components.forEach(Component::terminate);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            new SinglePlayerGame().run();
            return;
        }

        if (args[0].equalsIgnoreCase("server")) {
            int port = args.length >= 2 ? Integer.parseInt(args[1]) : 8880;
            new ServerGame(port).run();
            return;
        }

        if (args[0].equalsIgnoreCase("client")) {
            String host = args.length >= 2 ? args[1] : "localhost";
            int port = args.length >= 3 ? Integer.parseInt(args[2]) : 8880;
            new ClientGame(host, port).run();
            return;
        }

        throw new IllegalArgumentException(
            "Invalid arguments provided, use one of the following: \n"
            + "[no arguments] --> single player game\n"
            + "server [port:8880] --> server game\n"
            + "client [host:localhost] [port:8880] --> client game\n"
        );
    }

}
