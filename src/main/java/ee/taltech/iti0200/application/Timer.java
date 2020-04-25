package ee.taltech.iti0200.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.System.currentTimeMillis;

public class Timer {

    private final Logger logger = LogManager.getLogger(Timer.class);
    private final long tickDuration;

    private long lastLoopTime;
    private long tick = 0;
    private long delta = 0;

    /**
     * Input FPS to milliseconds for one tick duration
     */
    public Timer(long fps) {
        tickDuration = Math.round((1f / fps) * 1000) - 1;
    }

    public void initialize() {
        lastLoopTime = currentTimeMillis();
    }

    public long sleep() {
        long now = currentTimeMillis();
        long sleep = (tickDuration - (now - lastLoopTime)) + delta;

        if (sleep < 0) {
            delta = sleep;
            lastLoopTime = currentTimeMillis();
            return tick++;
        }

        delta = 0;

        if (sleep > 1) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ex) {
                logger.error("Sleep was interrupted", ex);
            }
        }

        lastLoopTime = currentTimeMillis();

        return tick++;
    }

}
