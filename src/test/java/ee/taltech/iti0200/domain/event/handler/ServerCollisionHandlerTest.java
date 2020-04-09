package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.Heal;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServerCollisionHandlerTest {

    private World world;
    private EventBus eventBus;
    private ServerCollisionHandler handler;

    @BeforeEach
    void setUp() {
        world = mock(World.class);
        eventBus = mock(EventBus.class);
        handler = new ServerCollisionHandler(world, eventBus);
    }

    @Test
    void handleRemovesProjectileAndDealsDamageOnHit() {
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        UUID projectileId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Projectile projectile = mock(Projectile.class);
        Player player = mock(Player.class);
        when(projectile.getId()).thenReturn(projectileId);
        when(player.getId()).thenReturn(playerId);
        when(world.getEntity(playerId)).thenReturn(player);
        when(world.getEntity(projectileId)).thenReturn(projectile);
        handler.handle(new EntityCollide(projectile, player, EVERYONE));

        verify(eventBus, times(2)).dispatch(captor.capture());
        List<Event> events = captor.getAllValues();
        assertThat(events.get(0)).isInstanceOf(DealDamage.class);
        assertThat(events.get(1)).isInstanceOf(RemoveEntity.class);
    }

    @Test
    void handleRemovesHealthGlobeAndHealsOnHit() {
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        UUID consumableId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Consumable consumable = mock(HealthGlobe.class);
        Player player = mock(Player.class);
        when(consumable.getId()).thenReturn(consumableId);
        when(player.getId()).thenReturn(playerId);
        when(world.getEntity(playerId)).thenReturn(player);
        when(world.getEntity(consumableId)).thenReturn(consumable);
        handler.handle(new EntityCollide(consumable, player, EVERYONE));

        verify(eventBus, times(2)).dispatch(captor.capture());
        List<Event> events = captor.getAllValues();
        assertThat(events.get(0)).isInstanceOf(Heal.class);
        assertThat(events.get(1)).isInstanceOf(RemoveEntity.class);
    }

}