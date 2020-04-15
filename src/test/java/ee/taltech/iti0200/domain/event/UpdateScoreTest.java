package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.domain.ScoreData;
import ee.taltech.iti0200.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateScoreTest {

    @Test
    void testToString() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        when(player1.getId()).thenReturn(UUID.fromString("216db3c6-4880-485c-aac9-6229e1295400"));
        when(player2.getId()).thenReturn(UUID.fromString("83ec4145-e451-4a65-91bf-e0fcdaa135b5"));

        Map<Player, ScoreData> scores = new HashMap<>();
        scores.put(player1, new ScoreData().setKills(1).setDeaths(3));
        scores.put(player2, new ScoreData().setKills(5).setDeaths(7));

        UpdateScore message = new UpdateScore(scores, null);

        assertThat(message.toString()).startsWith("UpdateScore{");
        assertThat(message.toString()).contains("216db3c6-4880-485c-aac9-6229e1295400:1/3");
        assertThat(message.toString()).contains("83ec4145-e451-4a65-91bf-e0fcdaa135b5:5/7");
    }

}
