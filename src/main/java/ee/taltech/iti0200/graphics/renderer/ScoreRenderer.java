package ee.taltech.iti0200.graphics.renderer;

import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.Camera;
import ee.taltech.iti0200.graphics.TextBox;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ScoreRenderer {

    private static final int DISPLAY_HEIGHT = 40;

    private final List<Pair<Player, TextBox>> scoreBoxes = new ArrayList<>();
    private final Score score;

    public ScoreRenderer(Score score) {
        this.score = score;
        update();
    }

    public void addPlayer(Player player) {
        TextBox textBox = new TextBox(0, DISPLAY_HEIGHT * scoreBoxes.size(), "", DISPLAY_HEIGHT);
        textBox.setText(player.getName() + " " + score.getKills(player) + " " + score.getDeaths(player));
        scoreBoxes.add(new MutablePair<>(player, textBox));
    }

    private void update() {
        scoreBoxes.clear();
        for (Player player: score.getPlayerScores().keySet()) {
            addPlayer(player);
        }
    }

    public void render(Alphabet alphabet, Camera camera) {
        if (score.getUpdated()) {
            score.setUpdated(false);
            update();
        }
        scoreBoxes.forEach(entry -> entry.getValue().render(alphabet, camera));
    }

}
