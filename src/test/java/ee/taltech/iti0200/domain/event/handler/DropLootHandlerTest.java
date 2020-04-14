package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.di.factory.ConsumableFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.server.DropLootHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DropLootHandlerTest {

    private World world;
    private EventBus eventBus;
    private DropLootHandler handler;
    private ConsumableFactory consumableFactory;

    @BeforeEach
    void setUp() {
        world = mock(World.class);
        eventBus = mock(EventBus.class);
        consumableFactory = mock(ConsumableFactory.class, RETURNS_DEEP_STUBS);
        handler = new DropLootHandler(world, consumableFactory, eventBus);
    }

    @Test
    void handlerDropsLoot() {
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        UUID victimId = UUID.randomUUID();

        Player victim = mock(Player.class, RETURNS_DEEP_STUBS);
        when(victim.getId()).thenReturn(victimId);
        when(world.getEntity(victimId)).thenReturn(victim);
        handler.handle(new DropLoot(victim, EVERYONE));

        verify(eventBus, times(1)).dispatch(captor.capture());
        List<Event> events = captor.getAllValues();
        assertThat(events.get(0)).isInstanceOf(CreateEntity.class);
    }

}