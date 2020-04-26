package ee.taltech.iti0200.menu;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.WindowId;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.input.KeyEvent;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MenuInput extends Input {

    private final Menu menu;

    @Inject
    public MenuInput(@WindowId long window, Menu menu) {
        super(window);
        this.menu = menu;
    }

    @Override
    public void initialize() {
        bind(new KeyEvent(GLFW_KEY_1, () -> menu.setGameMode(1), GLFW_RELEASE));
        bind(new KeyEvent(GLFW_KEY_2, () -> menu.setGameMode(2), GLFW_RELEASE));

        super.initialize();
    }

}
