package ee.taltech.iti0200.input;

import ee.taltech.iti0200.domain.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.vecmath.Vector2d;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class Input {

    private Logger logger;
    private Player player;
    private List<Runnable> events = new LinkedList<>();
    private Map<Integer, Map<Integer, Runnable>> bindings = new HashMap<>();

    public Input() {
        logger = LogManager.getLogger(Input.class);

        bindings.put(GLFW_KEY_A, new HashMap<>());
        bindings.get(GLFW_KEY_A).put(GLFW_PRESS, this::movePlayerLeft);
        bindings.get(GLFW_KEY_A).put(GLFW_REPEAT, this::movePlayerLeft);

        bindings.put(GLFW_KEY_D, new HashMap<>());
        bindings.get(GLFW_KEY_D).put(GLFW_PRESS, this::movePlayerRight);
        bindings.get(GLFW_KEY_D).put(GLFW_REPEAT, this::movePlayerRight);
    }

    public void initialize(long window, Player player) {
        this.player = player;

        glfwSetKeyCallback(window, (windowId, key, scanCode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowId, true);
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
        player.accelerate(new Vector2d(-1.0, 0.0));
        logger.debug("Player at: " + player.getBoundingBox().getCentre());
    }

    private void movePlayerRight() {
        player.accelerate(new Vector2d(1.0, 0.0));
        logger.debug("Player at: " + player.getBoundingBox().getCentre());
    }

}
