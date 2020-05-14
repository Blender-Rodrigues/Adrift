package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.event.EventBus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PhysicsTest {

    @Test
    void bodyDoesntFallThroughFloorWhenGivenLargeSpeed() {
        World world = mock(World.class);
        EventBus eventBus = mock(EventBus.class);
        Player player = new Player(new Vector(0, 5), world);
        Terrain terrain = new Terrain(new Vector(0, 0));
        Physics physics = new Physics(world, eventBus, null);

        when(world.getMovableBodies()).thenReturn(asList(player));
        when(world.getImMovableBodies()).thenReturn(asList(terrain));
        when(world.getTimeStep()).thenReturn(0.05);
        player.setSpeed(new Vector(0, -110));

        physics.update(1);

        assertThat(player.getBoundingBox().getCentre()).isEqualTo(new Vector(0, 1 - (1 - player.getBoundingBox().getSize().getY() * 2) / 2));
    }

    @Test
    void bodyFallsNextToWallAndAcceleratesTowardsWallWhenImpactToGround() {
        World world = mock(World.class);
        EventBus eventBus = mock(EventBus.class);
        Player player = new Player(new Vector(0.0625, 5), world);
        List<Entity> terrainBlocks = asList(
            new Terrain(new Vector(0, 0)),
            new Terrain(new Vector(1, 0)),
            new Terrain(new Vector(1, 1)),
            new Terrain(new Vector(1, 2)),
            new Terrain(new Vector(1, 3)),
            new Terrain(new Vector(1, 4)),
            new Terrain(new Vector(1, 5)),
            new Terrain(new Vector(1, 6))
        );
        Physics physics = new Physics(world, eventBus, null);

        when(world.getMovableBodies()).thenReturn(asList(player));
        when(world.getImMovableBodies()).thenReturn(terrainBlocks);
        when(world.getTimeStep()).thenReturn(0.05);

        for (int i = 0; i < 100; i++) {
            physics.update(i);
            player.moveRight();
            Vector location = player.getBoundingBox().getCentre();
            assertThat(location.getX()).isBetween((1 - player.getBoundingBox().getSize().getX() * 2 ) / 2 - 0.01, (1 - player.getBoundingBox().getSize().getY() * 2) / 2 + 0.01);
            assertThat(location.getY()).isGreaterThanOrEqualTo(player.getBoundingBox().getSize().getY() * 2);
        }
    }

}
