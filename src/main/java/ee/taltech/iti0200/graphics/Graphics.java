package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.di.annotations.MainShader;
import ee.taltech.iti0200.di.annotations.WindowId;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;

import static ee.taltech.iti0200.graphics.ViewPort.INITIAL_ZOOM_VALUE;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Graphics implements Component {

    private final long window;

    protected final ViewPort viewPort;

    protected Shader shader;
    protected int frameHeight;
    protected int frameWidth;

    @Inject
    public Graphics(@WindowId long window, ViewPort viewPort, @MainShader Shader shader) {
        this.window = window;
        this.viewPort = viewPort;
        this.shader = shader;
    }

    @Override
    public void initialize() throws IOException {
        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);
            frameHeight = pHeight.get(0);
            frameWidth = pWidth.get(0);
        } // the stack frame is popped automatically

        glfwSetFramebufferSizeCallback(window, (long window, int w, int h) -> {
            if (w > 0 && h > 0 && (frameWidth != w || frameHeight != h)) {
                frameWidth = w;
                frameHeight = h;
                viewPort.setHeight(h).setWidth(w).setZoom(viewPort.getZoom());
            }
        });

        viewPort.setWidth(frameWidth).setHeight(frameHeight).setZoom(INITIAL_ZOOM_VALUE);
        viewPort.setPosition(new Vector3f(0, 0, 0));

        initRenderers();

        glClearColor(0.3f, 0.3f, 0.3f, 0.0f);
    }

    @Override
    public void update(long tick) {
        glfwPollEvents();
        glViewport(0, 0, frameWidth, frameHeight);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        viewPort.update();
        updateRenderers(tick);

        glfwSwapBuffers(window); // swap the color buffers
    }

    @Override
    public void terminate() {
        glfwFreeCallbacks(window);
    }

    public boolean isWindowOpen() {
        return !glfwWindowShouldClose(window);
    }

    protected void initRenderers() throws IOException {
        // Implemented by children
    }

    protected void updateRenderers(long tick) {
        // Implemented by children
    }

}
