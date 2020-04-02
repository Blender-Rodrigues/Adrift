package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.di.factory.BotFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IntelligenceTest {

    @Test
    void spawnBotWhenZeroPlayers() {
        ArgumentCaptor<Bot> captor = ArgumentCaptor.forClass(Bot.class);
        UUID id = UUID.randomUUID();

        World world = mock(World.class);
        BotFactory botFactory = mock(BotFactory.class);
        Intelligence intelligence = new Intelligence(world, botFactory);
        Bot bot = mock(Bot.class);

        when(bot.getId()).thenReturn(id);
        when(botFactory.create()).thenReturn(bot);

        intelligence.update(0);

        verify(world).addEntity(captor.capture());
        Bot addedBot = captor.getValue();
        assertThat(addedBot.getId()).isEqualTo(id);
    }

    @Test
    void spawnBotAfterAddingPlayer() {
        ArgumentCaptor<Bot> captor = ArgumentCaptor.forClass(Bot.class);
        UUID id = UUID.randomUUID();

        Bot bot = mock(Bot.class);
        World world = mock(World.class);
        BotFactory botFactory = mock(BotFactory.class);
        Intelligence intelligence = new Intelligence(world, botFactory);

        when(bot.getId()).thenReturn(id);
        when(botFactory.create()).thenReturn(bot);

        intelligence.update(0);
        world.addEntity(mock(Player.class));
        intelligence.update(300);

        verify(world, times(3)).addEntity(captor.capture());
        List<Bot> addedBots = captor.getAllValues();
        assertThat(addedBots).hasSize(3);
        assertThat(addedBots.get(0).getId()).isEqualTo(id);
    }

    @Test
    void spawnBotAfterKillingBot() {
        ArgumentCaptor<Bot> captor = ArgumentCaptor.forClass(Bot.class);
        UUID id = UUID.randomUUID();

        Bot bot = mock(Bot.class);
        World world = mock(World.class, RETURNS_DEEP_STUBS);
        BotFactory botFactory = mock(BotFactory.class);
        Intelligence intelligence = new Intelligence(world, botFactory);

        when(bot.getId()).thenReturn(id);
        when(botFactory.create()).thenReturn(bot);

        intelligence.update(0);
        bot.setAlive(false);
        intelligence.update(300);

        verify(world, times(2)).addEntity(captor.capture());
        List<Bot> addedBots = captor.getAllValues();
        assertThat(addedBots).hasSize(2);
        assertThat(addedBots.get(1).getId()).isEqualTo(id);
    }

}