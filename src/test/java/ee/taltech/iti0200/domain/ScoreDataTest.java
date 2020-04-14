package ee.taltech.iti0200.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreDataTest {

    @Test
    void addKillIncrements() {
        ScoreData score = new ScoreData();

        score.setKills(4);
        score.addKill();

        assertThat(score.getKills()).isEqualTo(5);
    }

    @Test
    void addDeathsIncrements() {
        ScoreData score = new ScoreData();

        score.setDeaths(4);
        score.addDeath();

        assertThat(score.getDeaths()).isEqualTo(5);
    }

}
