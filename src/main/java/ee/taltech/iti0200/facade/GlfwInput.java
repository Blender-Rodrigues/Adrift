package ee.taltech.iti0200.facade;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.WindowId;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class GlfwInput {

    protected final long window;

    @Inject
    public GlfwInput(@WindowId long window) {
        this.window = window;
    }

    public void setKeyCallback(GLFWKeyCallbackI callback) {
        GLFW.glfwSetKeyCallback(window, callback);
    }

    public void setMouseButtonCallback(GLFWMouseButtonCallbackI callback) {
        GLFW.glfwSetMouseButtonCallback(window, callback);
    }

}
