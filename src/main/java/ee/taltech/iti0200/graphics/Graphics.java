package ee.taltech.iti0200.graphics;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.di.annotations.WindowId;
import ee.taltech.iti0200.graphics.renderer.Background;
import ee.taltech.iti0200.graphics.renderer.EntityRenderFacade;
import ee.taltech.iti0200.graphics.renderer.GuiRenderFacade;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;

import static ee.taltech.iti0200.graphics.Camera.INITIAL_ZOOM_VALUE;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Graphics implements Component {

    private final long window;
    private final Camera camera;
    private final EntityRenderFacade entityRenderer;
    private final GuiRenderFacade guiRenderer;
    private final Background backgroundRenderer;

    private Shader shader;
    private int frameHeight;
    private int frameWidth;

    @Inject
    public Graphics(@WindowId long window, Camera camera, EntityRenderFacade entityRenderer, GuiRenderFacade guiRenderer) {
        this.camera = camera;
        this.window = window;
        this.entityRenderer = entityRenderer;
        this.guiRenderer = guiRenderer;
        this.backgroundRenderer = new Background();
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

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - frameHeight) / 2,
                (vidmode.height() - frameWidth) / 2
            );

            camera.setWidth(frameWidth).setHeight(frameHeight).setZoom(INITIAL_ZOOM_VALUE);

        } // the stack frame is popped automatically

        glfwSetFramebufferSizeCallback(window, (long window, int w, int h) -> {
            if (w > 0 && h > 0 && (frameWidth != w || frameHeight != h)) {
                camera.setHeight(h).setWidth(w).setZoom(camera.getZoom());
                frameWidth = w;
                frameHeight = h;
            }
        });

        shader = new Shader("shader");

        camera.setPosition(new Vector3f(0, 0, 0));

        backgroundRenderer.initialize();
        entityRenderer.initialize();
        guiRenderer.initialize();

        glClearColor(0.3f, 0.3f, 0.3f, 0.0f);
    }

    @Override
    public void terminate() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public boolean isWindowOpen() {
        return !glfwWindowShouldClose(window);
    }

    @Override
    public void update(long tick) {
        glfwPollEvents();
        glViewport(0, 0, frameWidth, frameHeight);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        camera.update();

        backgroundRenderer.render(shader, camera, tick);
        entityRenderer.render(shader, camera, tick);
        guiRenderer.render(shader, camera, tick);

        glfwSwapBuffers(window); // swap the color buffers
    }

}
