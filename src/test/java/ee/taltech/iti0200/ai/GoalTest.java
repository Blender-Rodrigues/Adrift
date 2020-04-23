package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.di.factory.BotFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoalTest {

    World world;
    EventBus eventBus;
    Memory memory;

    @BeforeEach
    void setUp() {
        world = new World(-500, 500, -500, 500, 0.05);
        eventBus = mock(EventBus.class);
        memory = mock(Memory.class);
    }

    @ParameterizedTest
    @CsvSource({
        "0, 5, 0, -5",
        "5, 5, -5, -5",
        "5, 0, -5, 0",
        "5, -5, -5, 5",
        "0, -5, 0, 5",
        "-5, -5, 5, 5",
        "-5, 0, 5, 0",
        "-5, 5, 5, -5",
        "0, 5.5, 0, -5",
        "0, 4.5, 0, -5"
    })
    void cantSeeThroughCenteredTerrain(double botX, double botY, double targetX, double targetY) {
        Bot bot = mock(Bot.class);
        BoundingBox botBox = mock(BoundingBox.class);
        Vector botLocation = new Vector(botX, botY);

        world.addEntity(new Terrain(new Vector(0, 0)));
        world.mapTerrain();

        when(bot.getBoundingBox()).thenReturn(botBox);
        when(botBox.getCentre()).thenReturn(botLocation);

        Goal goal = new LookForPlayer(bot, world, eventBus, memory);

        assertFalse(goal.visible(new Vector(targetX, targetY)));
    }

    @Test
    void canSeeInEmptyWorld() {
        Bot bot = mock(Bot.class);
        BoundingBox botBox = mock(BoundingBox.class);
        Vector botLocation = new Vector(0, 5);

        world.mapTerrain();

        when(bot.getBoundingBox()).thenReturn(botBox);
        when(botBox.getCentre()).thenReturn(botLocation);

        Goal goal = new LookForPlayer(bot, world, eventBus, memory);

        assertTrue(goal.visible(new Vector(0, -5)));
    }
}