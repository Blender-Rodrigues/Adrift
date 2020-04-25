package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    void addKillAddsPlayerToScore() {
        Player player = mock(Player.class);
        assertThat(score.getPlayerScores()).isEmpty();
        score.addKill(player);
        assertThat(score.getPlayerScores().keySet()).containsExactly(player);
    }

    @Test
    void addDeathAddsPlayerToScore() {
        Player player = mock(Player.class);
        assertThat(score.getPlayerScores()).isEmpty();
        score.addDeath(player);
        assertThat(score.getPlayerScores().keySet()).containsExactly(player);
    }

    @Test
    void getSetUpdated() {
        assertThat(score.getUpdated()).isTrue();

        score.setUpdated(false);

        assertThat(score.getUpdated()).isFalse();
    }

}
