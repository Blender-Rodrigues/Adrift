package ee.taltech.iti0200.menu;

import com.google.inject.Inject;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.input.KeyEvent;
import ee.taltech.iti0200.facade.GlfwInput;

import static ee.taltech.iti0200.menu.Direction.DOWN;
import static ee.taltech.iti0200.menu.Direction.LEFT;
import static ee.taltech.iti0200.menu.Direction.RIGHT;
import static ee.taltech.iti0200.menu.Direction.UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MenuInput extends Input {

    private final Menu menu;

    @Inject
    public MenuInput(GlfwInput facade, Menu menu) {
        super(facade);
        this.menu = menu;
    }

    @Override
    public void initialize() {
        bind(new KeyEvent(GLFW_KEY_RIGHT, () -> menu.changeActive(RIGHT), GLFW_RELEASE));
        bind(new KeyEvent(GLFW_KEY_LEFT, () -> menu.changeActive(LEFT), GLFW_RELEASE));
        bind(new KeyEvent(GLFW_KEY_UP, () -> menu.changeActive(UP), GLFW_RELEASE));
        bind(new KeyEvent(GLFW_KEY_DOWN, () -> menu.changeActive(DOWN), GLFW_RELEASE));

        bind(new KeyEvent(GLFW_KEY_ENTER, this::interactWithActive, GLFW_RELEASE));
        bind(new KeyEvent(GLFW_KEY_KP_ENTER, this::interactWithActive, GLFW_RELEASE));

        super.initialize();
    }

    private void interactWithActive() {
        Runnable action = menu.getActive().getAction();
        if (action == null) {
            return;
        }
        action.run();
    }

    @Override
    protected void invokeKey(long window, int key, int scanCode, int action, int mods) {
        super.invokeKey(window, key, scanCode, action, mods);
        if (key < GLFW_KEY_SPACE || action != GLFW_RELEASE) {
            return;
        }

        MenuItem active = menu.getActive();
        if (!active.isWritable()) {
            return;
        }

        boolean keyInValidRange = (key >= GLFW_KEY_0 && key <= GLFW_KEY_SEMICOLON)
            || (key >= GLFW_KEY_A && key <= GLFW_KEY_Z)
            || key == GLFW_KEY_MINUS
            || key == GLFW_KEY_SPACE
            || key == GLFW_KEY_PERIOD
            || key == GLFW_KEY_BACKSPACE;

        if (!keyInValidRange) {
            return;
        }

        if (key == GLFW_KEY_BACKSPACE) {
            String value = active.getValue();
            if (value.isEmpty()) {
                return;
            }
            active.setValue(value.substring(0, value.length() - 1));
            return;
        }

        if (active.equals(menu.getPort())) {
            if (key < GLFW_KEY_0 || key > GLFW_KEY_9) {
                return;
            }
        }

        active.setValue(active.getValue() + (char) key);
    }

}
