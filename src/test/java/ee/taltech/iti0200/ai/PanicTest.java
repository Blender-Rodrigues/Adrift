package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Random;

import static ee.taltech.iti0200.network.message.Receiver.SERVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PanicTest {

    @Test
    void panicShootsAtSound() {
        ArgumentCaptor<GunShot> captor = ArgumentCaptor.forClass(GunShot.class);

        Bot bot = mock(Bot.class);
        World world = mock(World.class);
        EventBus eventBus = mock(EventBus.class);
        Gun shootingGun = mock(Gun.class);
        Gun botGun = mock(Gun.class);
        BoundingBox boundingBox = mock(BoundingBox.class);
        Random random = mock(Random.class);
        Panic panic = new Panic(bot, world, eventBus, random);

        when(bot.getActiveGun()).thenReturn(botGun);
        when(bot.getBoundingBox()).thenReturn(boundingBox);
        when(boundingBox.getCentre()).thenReturn(new Vector(0, 0));
        when(random.nextBoolean()).thenReturn(true);
        when(bot.canShoot(0L)).thenReturn(true);

        long adrenaline = panic.react(0L, Sensor.AUDIO, new Vector(2, 0), new Vector(1, 0), shootingGun);
        assertThat(adrenaline).isEqualTo(8);
        verify(eventBus).dispatch(captor.capture());
        GunShot shot = captor.getValue();
        assertThat(shot.getGun()).isEqualTo(botGun);
        assertThat(shot.getDirection()).isEqualTo(new Vector(1, 0));
        assertThat(shot.getReceiver()).isEqualTo(SERVER);
    }
}