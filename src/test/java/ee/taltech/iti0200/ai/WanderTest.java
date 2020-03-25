package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WanderTest {

    @Test
    void reactRaisesAdrenalineForGunshotDistance() {
        Gun gun = mock(Gun.class);
        Bot bot = mock(Bot.class, RETURNS_DEEP_STUBS);
        when(bot.getBoundingBox().getCentre()).thenReturn(new Vector(40, 51));

        Wander wander = new Wander(bot, null);

        long actual = wander.react(0, AUDIO, new Vector(10, 42), new Vector(0, 0), gun);

        assertThat(actual).isEqualTo(18);
    }

}
