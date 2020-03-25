package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import javax.vecmath.Vector2d;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Mostly still the  hello-world example from https://www.lwjgl.org/guide just to get
 * a rough idea on how to start using LWJGL library
 */
public class Graphics implements Component {

    public static final String DEFAULT = "DEFAULT";
    public static final HashMap<Class<? extends Body>, HashMap<String, Supplier<Renderer>>> renderers = new HashMap<>();

    private long window;

    private World world;
    private Shader shader;
    private Camera camera;

    public Graphics(World world, Player player) {
        this.world = world;
        this.camera = new Camera(1200, 800, player);

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
        window = glfwCreateWindow(1200, 800, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
    }

    public long getWindow() {
        return window;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void initialize() throws IOException {

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader = new Shader("shader");

        camera.setPosition(new Vector3f(0, 0, 0));

        createRenderers();

        world.getEntities().forEach(Graphics::setRenderer);

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

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        camera.update();

        Matrix4f rotation = new Matrix4f();
        world.getEntities().stream()
            .filter(entity -> !(entity instanceof Gun))
            .forEach(entity -> entity.render(shader, camera, tick, rotation));

        rotation.setRotationXYZ(0F, 0F, 1F);
        world.getEntities().stream()
            .filter(entity -> entity instanceof Gun)
            .forEach(entity -> entity.render(shader, camera, tick, getRotation(((Gun) entity).getPointedAt())));

        glfwSwapBuffers(window); // swap the color buffers
    }

    private Matrix4f getRotation(Vector pointedAt) {
        float rotationAngle = - (float) (Math.atan2(pointedAt.getX(), pointedAt.getY()));
        Matrix4f rotationMatrix;
        if (rotationAngle < 0) {
            return new Matrix4f().setRotationXYZ(0F, 0F, rotationAngle + (float) Math.PI / 2);
        }
        Matrix4f mirrorMatrix = new Matrix4f().m00(-1);
        rotationMatrix = new Matrix4f().setRotationXYZ(0F, 0F, - rotationAngle + (float) Math.PI / 2);
        rotationMatrix = mirrorMatrix.mul(rotationMatrix);
        return rotationMatrix;
    }

    private void createRenderers() {
        Texture defaultTexture = new Texture("", "default");
        Texture gunTexture = new Texture("animations/gun/", "shotgun");
        Animation playerDefault = new Animation(2, "animations/player/", "player.default", 20);
        Animation playerJump = new Animation(2, "animations/player/", "player.jump", 20);
        Animation botDefault = new Animation(2, "animations/bot/", "bot.default", 20);

        HashMap<String, Supplier<Renderer>> defaultRenderer = new HashMap<>();
        defaultRenderer.put(DEFAULT, () -> new Drawable(defaultTexture));
        renderers.put(Entity.class, defaultRenderer);

        HashMap<String, Supplier<Renderer>> gunRenderer = new HashMap<>();
        gunRenderer.put(DEFAULT, () -> new Drawable(gunTexture));
        renderers.put(Gun.class, gunRenderer);

        HashMap<String, Supplier<Renderer>> playerRenderer = new HashMap<>();
        playerRenderer.put(DEFAULT, () -> new Animateable(playerDefault));
        playerRenderer.put("jump", () -> new Animateable(playerJump));
        renderers.put(Player.class, playerRenderer);

        HashMap<String, Supplier<Renderer>> botRenderer = new HashMap<>();
        botRenderer.put(DEFAULT, () -> new Animateable(botDefault));
        renderers.put(Bot.class, botRenderer);
    }

    public static void setRenderer(Entity entity) {
        HashMap<String, Renderer> map = renderers.getOrDefault(entity.getClass(), renderers.get(Entity.class))
            .entrySet()
            .stream()
            .collect(toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get().setEntity(entity).initialize(),
                (a, b) -> b,
                HashMap::new
            ));
        entity.setRenderers(map);
    }

}
