package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LookForPlayerTest {

    private World world;
    private Player player;
    private Bot bot;
    private Gun gun;
    private Vector playerLocation;
    private Vector direction;

    @BeforeEach
    void setUp() {
        world = new World(0, 0, 0, 0, 0);

        player = mock(Player.class, RETURNS_DEEP_STUBS);
        bot = mock(Bot.class, RETURNS_DEEP_STUBS);
        gun = mock(Gun.class);

        Vector botLocation = new Vector(36.25, 41.25);
        playerLocation = new Vector(43.0, 49.0);

        direction = new Vector(playerLocation);
        direction.sub(botLocation);
        direction.normalize();

        when(bot.getBoundingBox().getCentre()).thenReturn(botLocation);
        when(player.getBoundingBox().getCentre()).thenReturn(playerLocation);
    }

    @Test
    void reactLooksTowardsGunshotAndDoesNotSeePlayerIfTerrainIsInBetween() {
        // given
        world.addEntity(player);
        world.addEntity(bot);
        world.addEntity(new Terrain(new Vector(40, 45)));
        world.addEntity(new Terrain(new Vector(40, 46)));
        world.addEntity(new Terrain(new Vector(40, 47)));
        world.addEntity(new Terrain(new Vector(39, 47)));
        world.initialize();

        // when
        LookForPlayer goal = new LookForPlayer(bot, world, mock(EventBus.class), mock(Memory.class));
        long reaction = goal.react(0, AUDIO, playerLocation, direction, gun);

        // then
        assertThat(reaction).isEqualTo(9);
        verify(bot.getBrain(), never()).updateSensor(any(), any(), any());
    }

    @Test
    void reactLooksTowardsGunshotAndSeesPlayerIfNoTerrainInBetween() {
        // given
        world.addEntity(player);
        world.addEntity(bot);
        world.addEntity(new Terrain(new Vector(43, 46)));
        world.addEntity(new Terrain(new Vector(35, 40)));
        world.addEntity(new Terrain(new Vector(36, 40)));
        world.addEntity(new Terrain(new Vector(37, 40)));
        world.initialize();

        // when
        LookForPlayer goal = new LookForPlayer(bot, world, mock(EventBus.class), mock(Memory.class));
        long reaction = goal.react(0, AUDIO, playerLocation, direction, gun);

        // then
        assertThat(reaction).isEqualTo(9);
        verify(bot.getBrain()).updateSensor(Sensor.VISUAL, playerLocation, player);
    }

}
