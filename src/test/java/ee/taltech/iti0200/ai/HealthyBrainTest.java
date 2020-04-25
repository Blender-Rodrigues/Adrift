package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HealthyBrainTest {

    HealthyBrain brain;
    World world;
    Bot bot;
    TreeMap<Long, Goal> goals;
    Runnable onDeath;
    Wander wanderGoal;

    @BeforeEach
    void setUp() {
        bot = mock(Bot.class);
        world = mock(World.class);
        goals = new TreeMap<>();
        wanderGoal = mock(Wander.class);
        goals.put(0L, wanderGoal);
        onDeath = mock(Runnable.class);
        brain = new HealthyBrain(world);
    }

    @Test
    void bindAndToString() {
        brain.bind(bot, goals, onDeath);
        when(bot.toString()).thenReturn("B1");
        when(wanderGoal.toString()).thenReturn("A1");
        assertThat(brain.toString()).isEqualTo("Brain{bot=B1, active=A1, adrenaline=0}");
    }

    @Test
    void followGoal() {
        brain.bind(bot, goals, onDeath);
        when(bot.isAlive()).thenReturn(true);
        brain.followGoal(0L);
        verify(wanderGoal).execute(0L);
    }

    @Test
    void updateSensor() {
        brain.bind(bot, goals, onDeath);
        when(bot.isAlive()).thenReturn(true);
        Bot otherBot = mock(Bot.class);
        BoundingBox boundingBox = mock(BoundingBox.class);
        when(bot.getBoundingBox()).thenReturn(boundingBox);
        when(boundingBox.getCentre()).thenReturn(new Vector(0, 0));
        when(world.getTime()).thenReturn(1L);

        brain.updateSensor(Sensor.VISUAL, new Vector(5, 0), otherBot);
        verify(wanderGoal).react(1L, Sensor.VISUAL, new Vector(5, 0), new Vector(1, 0), otherBot);
    }
}