package ee.taltech.iti0200.domain.event.handler.client;

import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.ScoreData;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.UpdateScore;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateScoreHandlerTest {

    @Test
    void handleUpdatesLocalScoresWithServerValues() {
        World world = mock(World.class);
        Score score = mock(Score.class);
        UpdateScore event = mock(UpdateScore.class);

        UUID existingId = UUID.randomUUID();
        UUID newId = UUID.randomUUID();
        UUID removedId = UUID.randomUUID();

        Player existingPlayer = mock(Player.class);
        Player newPlayer = mock(Player.class);
        Player removedPlayer = mock(Player.class);

        when(world.getEntity(existingId)).thenReturn(existingPlayer);
        when(world.getEntity(newId)).thenReturn(newPlayer);
        when(world.getEntity(removedId)).thenReturn(null);

        Map<Player, ScoreData> local = new HashMap<>();
        local.put(existingPlayer, new ScoreData().setKills(1).setDeaths(3));
        local.put(removedPlayer, new ScoreData().setKills(11).setDeaths(33));

        Map<UUID, Integer[]> server = new HashMap<>();
        server.put(existingId, new Integer[]{5, 7});
        server.put(newId, new Integer[]{17, 19});

        when(event.getScores()).thenReturn(server);
        when(score.getPlayerScores()).thenReturn(local);

        new UpdateScoreHandler(world, score).handle(event);

        verify(score).setUpdated(true);

        assertThat(local).hasSize(3);
        assertThat(local.get(existingPlayer).getKills()).isEqualTo(5);
        assertThat(local.get(existingPlayer).getDeaths()).isEqualTo(7);
        assertThat(local.get(removedPlayer).getKills()).isEqualTo(11);
        assertThat(local.get(removedPlayer).getDeaths()).isEqualTo(33);
        assertThat(local.get(newPlayer).getKills()).isEqualTo(17);
        assertThat(local.get(newPlayer).getDeaths()).isEqualTo(19);
    }

}
