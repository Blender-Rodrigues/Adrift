package ee.taltech.iti0200.di;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Singleton;
import ee.taltech.iti0200.di.annotations.WindowId;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Mouse;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GuiModule extends AbstractModule {

    private static final int INITIAL_SCREEN_WIDTH = 1200;
    private static final int INITIAL_SCREEN_HEIGHT = 800;
    private static final String WINDOW_GAME_NAME = "Escape From Eros";

    private long windowId;

    protected void configure() {
        initialize();

        bind(Key.get(Long.class, WindowId.class)).toInstance(windowId);

        bind(Camera.class).in(Singleton.class);
        bind(Graphics.class).in(Singleton.class);
        bind(Mouse.class).in(Singleton.class);
    }

    private void initialize() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        windowId = glfwCreateWindow(INITIAL_SCREEN_WIDTH, INITIAL_SCREEN_HEIGHT, WINDOW_GAME_NAME, NULL, NULL);
        if (windowId == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
    }

}
