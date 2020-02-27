package ee.taltech.iti0200.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {

    private static final long THREAD_SLEEP = 10;

    private Logger logger;
    private double lastLoopTime;
    private float sleepTime;
    private long tick = 0;

    public Timer(float fps) {
        sleepTime = 1F / fps;
        logger = LogManager.getLogger(Timer.class);
    }

    public void initialize() {
        lastLoopTime = getTime();
    }

    public long sleep() {
        double now = getTime();

        while (now - lastLoopTime < sleepTime) {
            Thread.yield();

            try {
                Thread.sleep(THREAD_SLEEP);
            } catch (InterruptedException ex) {
                logger.error("Sleep was interrupted", ex);
            }

            now = getTime();
        }

        lastLoopTime = getTime();

        return tick++;
    }

    private double getTime() {
        return glfwGetTime();
    }

}
