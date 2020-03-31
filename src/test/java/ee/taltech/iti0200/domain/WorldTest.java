package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Living;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WorldTest {

    private World world;

    @BeforeEach
    void setUp() {
        world = new World(0, 0, 0, 0, 0);
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

}
