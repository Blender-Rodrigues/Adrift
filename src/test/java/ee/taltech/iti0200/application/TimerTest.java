package ee.taltech.iti0200.application;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class TimerTest {

    private static final Random RANDOM = new Random();
    private static final int RANDOM_RANGE = 20;
    private static final double AVG_TOLERANCE = 0.1;
    private static final int SAMPLE_SIZE = 30;

    @ParameterizedTest
    @ValueSource(longs = {30, 40, 50, 60, 80})
    void sleepTriesToKeepExpectedTickRate(long fps) throws InterruptedException {
        List<Long> measurements = new LinkedList<>();
        float expectedAverage = 1f / fps * 1000;

        Timer timer = new Timer(fps);
        timer.initialize();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            long before = System.currentTimeMillis();

            Thread.sleep(RANDOM.nextInt(RANDOM_RANGE));

            timer.sleep();

            measurements.add(System.currentTimeMillis() - before);
        }

        double actualAverage = measurements.stream().mapToLong(x -> x).summaryStatistics().getAverage();

        assertThat(actualAverage).isBetween(expectedAverage * (1 - AVG_TOLERANCE), expectedAverage * (1 + AVG_TOLERANCE));
    }

}
