package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.di.factory.BotFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IntelligenceTest {

    private ArgumentCaptor<CreateEntity> captor;

    private World world;
    private BotFactory botFactory;
    private EventBus eventBus;

    private List<Living> entities;
    private Intelligence intelligence;

    @BeforeEach
    void setUp() {
        captor = ArgumentCaptor.forClass(CreateEntity.class);
        entities = new ArrayList<>();

        world = mock(World.class);
        botFactory = mock(BotFactory.class);
        eventBus = mock(EventBus.class);

        when(world.getLivingEntities()).thenReturn(entities);

        intelligence = new Intelligence(world, botFactory, eventBus);
    }

    @Test
    void spawnBotWhenZeroPlayers() {
        UUID id = UUID.randomUUID();
        Bot bot = mock(Bot.class);

        when(bot.getId()).thenReturn(id);
        when(botFactory.create()).thenReturn(bot);

        intelligence.update(0);

        verify(eventBus).dispatch(captor.capture());
        CreateEntity event = captor.getValue();
        assertThat(event.getId()).isEqualTo(id);
    }

    @Test
    void spawnBotAfterAddingPlayer() {
        UUID id = UUID.randomUUID();
        Bot bot = mock(Bot.class);

        when(bot.getId()).thenReturn(id);
        when(botFactory.create()).thenReturn(bot);

        intelligence.update(0);

        entities.add(mock(Player.class));
        entities.add(bot);
        entities.add(bot);

        intelligence.update(300);
        intelligence.update(600);

        verify(eventBus, times(3)).dispatch(captor.capture());
        CreateEntity event = captor.getValue();
        assertThat(event.getId()).isEqualTo(id);
    }

    @Test
    void spawnBotAfterKillingBot() {
        UUID id = UUID.randomUUID();
        Bot bot = mock(Bot.class);

        when(bot.getId()).thenReturn(id);
        when(botFactory.create()).thenReturn(bot);

        intelligence.update(0);
        bot.setAlive(false);
        intelligence.update(300);

        verify(eventBus, times(2)).dispatch(captor.capture());
        CreateEntity event = captor.getValue();
        assertThat(event.getId()).isEqualTo(id);
    }

}
