package ee.taltech.iti0200.graphics;

import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.entity.Player;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreRenderer {

    private Score score;
    private List<Pair<Player, TextBox>> scoreBoxes;
    private int DISPLAY_HEIGHT = 40;

    public ScoreRenderer(Score score) {
        this.score = score;
        update();
    }

    public void addPlayer(Player player) {
        TextBox textBox = new TextBox(0, DISPLAY_HEIGHT * scoreBoxes.size(), "", DISPLAY_HEIGHT);
        textBox.setText(scoreBoxes.size() + " " + score.getKills(player) + " " + score.getDeaths(player));
        scoreBoxes.add(new MutablePair<>(player, textBox));
    }

    private void update() {
        scoreBoxes = new ArrayList<>();
        for (Player player: score.getPlayerScores().keySet()) {
            addPlayer(player);
        }
    }

    public void render(Alphabet alphabet, Camera camera) {
        if (score.getupdated()) {
            score.setUpdated(false);
            update();
        }
        for (Pair entry: scoreBoxes) {
            ((TextBox) entry.getValue()).render(alphabet, camera);
        }
    }
}
