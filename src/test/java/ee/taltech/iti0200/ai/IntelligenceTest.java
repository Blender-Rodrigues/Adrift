package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.di.factory.BotFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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

}