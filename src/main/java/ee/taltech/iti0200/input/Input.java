package ee.taltech.iti0200.input;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.application.RestartGame;
import ee.taltech.iti0200.facade.GlfwInput;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_8;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class Input implements Component {

    protected final GlfwInput facade;
    protected final Set<KeyEvent> events = new HashSet<>();
    protected final Map<Integer, KeyEvent> bindings = new HashMap<>();

    protected long currentTick;

    @Inject
    public Input(GlfwInput facade) {
        this.facade = facade;
    }

    @Override
    public void initialize() {
        facade.setKeyCallback(this::invokeKey);
        facade.setMouseButtonCallback(this::invokeMouse);
    }

    public void update(long tick) {
        currentTick = tick;
        Iterator<KeyEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            KeyEvent event = iterator.next();
            event.event.run();
            if (!event.actions.contains(GLFW_REPEAT)) {
                iterator.remove();
            }
        }
    }

    protected void bind(KeyEvent event) {
        bindings.put(event.key, event);
    }

    protected void invokeKey(long window, int key, int scanCode, int action, int mods) {
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
            throw new RestartGame("Escape was pressed");
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
