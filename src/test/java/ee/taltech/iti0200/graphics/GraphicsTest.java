package ee.taltech.iti0200.graphics;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import ee.taltech.iti0200.di.CommonModule;
import ee.taltech.iti0200.di.GuiModule;
import ee.taltech.iti0200.di.annotations.WindowId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.lwjgl.opengl.GL;

import static java.util.Arrays.asList;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

/**
 * Use this parent class to provide gl context if you're not testing graphics components.
 * Don't use it together with Graphics class itself as it creates its own context.
 */
@GuiTest
abstract public class GraphicsTest {

    private static Long window;

    @BeforeAll
    static void beforeAll() {
        Injector injector = Guice.createInjector(asList(new CommonModule(), new GuiModule()));
        window = injector.getInstance(Key.get(Long.class, WindowId.class));

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    @AfterAll
    static void afterAll() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

}
