package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayDeque;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LayoutTest {

    @SuppressWarnings("unchecked")
    @Test
    void populateWorld() throws IOException {
        ArgumentCaptor<ArrayDeque<Vector>> spawnCaptor = ArgumentCaptor.forClass(ArrayDeque.class);
        ArgumentCaptor<Terrain> terrainCaptor = ArgumentCaptor.forClass(Terrain.class);

        World world = mock(World.class);

        new Layout("station_small.jpg").populateWorld(world);

        verify(world).setSpawnPoints(spawnCaptor.capture());
        verify(world, times(3901)).addEntity(terrainCaptor.capture());

        ArrayDeque<Vector> spawns = spawnCaptor.getValue();

        assertThat(spawns).containsExactlyInAnyOrder(
            new Vector(7, 26),
            new Vector(134, 69),
            new Vector(179, 44),
            new Vector(142, 19),
            new Vector(128, 41),
            new Vector(85, 72),
            new Vector(155, 59),
            new Vector(57, 21),
            new Vector(107, 54),
            new Vector(103, 42),
            new Vector(35, 55),
            new Vector(87, 20),
            new Vector(116, 72),
            new Vector(172, 62),
            new Vector(53, 63),
            new Vector(34, 20)
        );
    }

}
