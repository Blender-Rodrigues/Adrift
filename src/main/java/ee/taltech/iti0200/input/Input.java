package ee.taltech.iti0200.input;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.Player;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Input implements Component {

    private Logger logger;
    private Player player;
    private long window;
    private List<Runnable> events = new LinkedList<>();
    private Map<Integer, Map<Integer, Runnable>> bindings = new HashMap<>();
    private Graphics graphics;

    public Input(long window, Player player, Graphics graphics) {
        logger = LogManager.getLogger(Input.class);
        this.player = player;
        this.window = window;
        this.graphics = graphics;
    }

    public void initialize() {
        bindings.put(GLFW_KEY_A, new HashMap<>());
        bindings.get(GLFW_KEY_A).put(GLFW_PRESS, this::movePlayerLeft);
        bindings.get(GLFW_KEY_A).put(GLFW_REPEAT, this::movePlayerLeft);

        bindings.put(GLFW_KEY_D, new HashMap<>());
        bindings.get(GLFW_KEY_D).put(GLFW_PRESS, this::movePlayerRight);
        bindings.get(GLFW_KEY_D).put(GLFW_REPEAT, this::movePlayerRight);

        bindings.put(GLFW_KEY_RIGHT, new HashMap<>());
        bindings.get(GLFW_KEY_RIGHT).put(GLFW_PRESS, this::moveCameraRight);

        bindings.put(GLFW_KEY_LEFT, new HashMap<>());
        bindings.get(GLFW_KEY_LEFT).put(GLFW_PRESS, this::moveCameraLeft);

        bindings.put(GLFW_KEY_UP, new HashMap<>());
        bindings.get(GLFW_KEY_UP).put(GLFW_PRESS, this::moveCameraUp);

        bindings.put(GLFW_KEY_DOWN, new HashMap<>());
        bindings.get(GLFW_KEY_DOWN).put(GLFW_PRESS, this::moveCameraDown);


        glfwSetKeyCallback(window, (window, key, scanCode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
            if (bindings.containsKey(key) && bindings.get(key).containsKey(action)) {
                events.add(bindings.get(key).get(action));
            }
        });
    }

    public void update(long tick) {
        events.forEach(Runnable::run);
        events.clear();
    }

    private void movePlayerLeft() {
        player.accelerate(new Vector(-1.0, 0.0));
        logger.debug("Player at: " + player.getBoundingBox().getCentre());
    }

    private void movePlayerRight() {
        player.accelerate(new Vector(1.0, 0.0));
        logger.debug("Player at: " + player.getBoundingBox().getCentre());
    }

    private void moveCameraLeft() {
        graphics.moveCameraLeft();
    }

    private void moveCameraRight() {
        graphics.moveCameraRight();
    }

    private void moveCameraUp() {
        graphics.moveCameraUp();
    }

    private void moveCameraDown() {
        graphics.moveCameraDown();
    }

}
