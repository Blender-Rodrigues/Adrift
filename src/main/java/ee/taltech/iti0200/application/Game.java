package ee.taltech.iti0200.application;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.menu.MenuWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

import static ee.taltech.iti0200.application.Component.priorities;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;

abstract public class Game {

    private final Logger logger = LogManager.getLogger(Game.class);
    private final Timer timer;

    protected EventBus eventBus;
    protected World world;
    protected List<Component> components = new LinkedList<>();

    private long tick = 0;

    public Game(World world, EventBus eventBus, Timer timer) {
        this.world = world;
        this.timer = timer;
        this.eventBus = eventBus;
        Thread.currentThread().setName(getClass().getSimpleName());
    }

    public void run() {
        logger.info("Initializing game");
        try {
            initialize();
            timer.initialize();

            components.sort(comparingInt(component -> priorities.getOrDefault(component.getClass(), 0)));

            for (Component component : components) {
                long start = System.currentTimeMillis();
                component.initialize();
                logger.debug(
                    "Initialized {} in {}ms",
                    component.getClass().getSimpleName(),
                    System.currentTimeMillis() - start
                );
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
                components.forEach(component -> component.update(tick));
                world.update(tick);
                loop(tick);
                tick = timer.sleep();
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
        MenuWrapper.main(args);
    }

}
