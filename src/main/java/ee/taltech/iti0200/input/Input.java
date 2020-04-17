package ee.taltech.iti0200.input;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.di.annotations.WindowId;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.graphics.Camera;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
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
    private EventBus eventBus;
    private long currentTick;

    @Inject
    public Input(
        @WindowId long window,
        @LocalPlayer Player player,
        Camera camera,
        Mouse mouse,
        EventBus eventBus
    ) {
        this.player = player;
        this.window = window;
        this.camera = camera;
        this.mouse = mouse;
        this.eventBus = eventBus;
    }

    public void initialize() {
        bind(new KeyEvent(GLFW_KEY_A, player::moveLeft, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_D, player::moveRight, GLFW_PRESS, GLFW_REPEAT));
        bind(new KeyEvent(GLFW_KEY_W, player::jump, GLFW_PRESS));
        bind(new KeyEvent(GLFW_MOUSE_BUTTON_LEFT, this::playerShoot, GLFW_PRESS, GLFW_REPEAT));

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
        currentTick = tick;
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

    private void playerShoot() {
        Gun gun = player.getActiveGun();
        if (player.isAlive() && gun != null && gun.canShoot(currentTick)) {
            eventBus.dispatch(new GunShot(gun, player.getLookingAt(), EVERYONE));
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
