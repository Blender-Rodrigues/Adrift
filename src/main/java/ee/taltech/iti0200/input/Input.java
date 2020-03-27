package ee.taltech.iti0200.input;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.physics.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_8;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class Input implements Component {

    private Player player;
    private long window;
    private Set<KeyEvent> events = new HashSet<>();
    private Map<Integer, KeyEvent> bindings = new HashMap<>();
    private Camera camera;
    private Mouse mouse;

    public Input(long window, Player player, Camera camera) {
        this.player = player;
        this.window = window;
        this.camera = camera;
        this.mouse = new Mouse(this.camera, this.window);
    }

    public void initialize() {
        bind(new KeyEvent(GLFW_KEY_A, this::playerMoveLeft, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_D, this::playerMoveRight, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_W, this::playerJump, GLFW_PRESS));
        bind(new KeyEvent(GLFW_MOUSE_BUTTON_LEFT, this::playerShoot, GLFW_PRESS));

        bind(new KeyEvent(GLFW_KEY_RIGHT, camera::moveRight, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_LEFT, camera::moveLeft, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_UP, camera::moveUp, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_DOWN, camera::moveDown, GLFW_PRESS, GLFW_REPEAT));

        bind(new KeyEvent(GLFW_KEY_I, camera::zoomIn, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_O, camera::zoomOut, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_F, camera::togglePlayerCam, GLFW_PRESS));

        glfwSetKeyCallback(window, this::invokeKey);
        glfwSetMouseButtonCallback(window, this::invokeMouse);
    }

    public void update(long tick) {
        Iterator<KeyEvent> iterator = events.iterator();
        updateMouse();
        while (iterator.hasNext()) {
            KeyEvent event = iterator.next();
            event.event.run();
            if (!event.actions.contains(GLFW_REPEAT)) {
                iterator.remove();
            }
        }
    }

    private void playerMoveLeft() {
        if (!player.isAlive()) {
            return;
        }
        if (player.isOnFloor()) {
            player.accelerate(new Vector(-0.5, 0.0));
        } else {
            player.accelerate(new Vector(-0.2, 0.0));
        }
    }

    private void playerMoveRight() {
        if (!player.isAlive()) {
            return;
        }
        if (player.isOnFloor()) {
            player.accelerate(new Vector(0.5, 0.0));
        } else {
            player.accelerate(new Vector(0.2, 0.0));
        }
    }

    private void playerJump() {
        if (player.isAlive() && player.getJumpsLeft() > 0) {
            player.setJumpsLeft(player.getJumpsLeft() - 1);
            player.accelerate(new Vector(0.0, player.getJumpDeltaV()));
        }
    }

    private void playerShoot() {
        if (player.isAlive()) {
            player.shoot();
        }
    }

    private void updateMouse() {
        mouse.update();
        player.setLookingAt(mouse.getPhysicsPosition());
    }

    private void bind(KeyEvent event) {
        bindings.put(event.key, event);
    }

    private void invokeKey(long window, int key, int scanCode, int action, int mods) {
        if (key >= GLFW_KEY_SPACE) {
            invoke(window, key, scanCode, action, mods);
        }
    }

    private void invokeMouse(long window, int key, int action, int mods) {
        if (key <= GLFW_MOUSE_BUTTON_8) {
            invoke(window, key, 0, action, mods);
        }
    }

    private void invoke(long window, int key, int scanCode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }

        if (!bindings.containsKey(key)) {
            return;
        }

        KeyEvent event = bindings.get(key);

        if (event.actions.contains(action)) {
            events.add(event);
        } else if (action == GLFW_RELEASE) {
            events.remove(event);
        }
    }

}
