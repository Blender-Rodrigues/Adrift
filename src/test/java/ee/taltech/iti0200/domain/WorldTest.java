package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    @CsvSource({
        "-50,50,true",
        "-1,50,true",
        "101,50,true",
        "1500,50,true",
        "50,-50,true",
        "50,-1,true",
        "50,101,true",
        "50,1500,true",
        "50,50,false",
        "0,0,false",
        "90,90,false"
    })
    void testEntityOutOfBoundsByX(int x, int y, boolean expected) {
        BoundingBox boundingBox = new BoundingBox(new Vector(x, y), new Vector(10, 10));

        boolean actual = world.entityOutOfBounds(new Entity(0, boundingBox));

        assertThat(actual).isEqualTo(expected);
    }

}
