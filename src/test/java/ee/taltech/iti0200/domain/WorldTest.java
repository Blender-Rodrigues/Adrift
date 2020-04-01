package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WorldTest {

    private World world;

    @BeforeEach
    void setUp() {
        world = new World(0, 100, 0, 100, 0);
    }

    @Test
    void removeEntitySetsLivingToDead() {
        // given
        Bot bot = mock(Bot.class);
        world.addEntity(bot);

        // when
        world.removeEntity(bot);

        // then
        long removed = world.getEntitiesRemoved();
        List<Living> living = world.getLivingEntities();

        assertThat(removed).isEqualTo(1);
        assertThat(living).isEmpty();
        verify(bot).setAlive(false);
    }

    @ParameterizedTest
    @ValueSource(ints = {-50, -1, 101, 150})
    void testEntityOutOfBoundsByX(int xPosition) {
        assertTrue(world.entityOutOfBounds(new Entity(0, new BoundingBox(
            new Vector(xPosition, 50),
            new Vector(10, 10)
        ))));
    }

    @ParameterizedTest
    @ValueSource(ints = {-50, -1, 101, 150})
    void testEntityOutOfBoundsByY(int yPosition) {
        assertTrue(world.entityOutOfBounds(new Entity(0, new BoundingBox(
            new Vector(50, yPosition),
            new Vector(10, 10)
        ))));
    }

    @Test
    void testEntityInBounds() {
        assertFalse(world.entityOutOfBounds(new Entity(0, new BoundingBox(
            new Vector(50, 50),
            new Vector(10, 10)
        ))));
    }

}
