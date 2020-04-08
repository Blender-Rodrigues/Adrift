package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.graphics.ScoreRenderer;

import java.util.HashMap;
import java.util.Map;

public class Score {

    private Map<Player, ScoreData> players;
    private boolean updated;

    public Score() {
        players = new HashMap<>();
        updated = true;
    }

    public boolean getupdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void addPlayer(Player player) {
        players.put(player, new ScoreData());
        updated = true;
    }

    public void addKill(Player player) {
        if (!players.containsKey(player)) {
            addPlayer(player);
        }
        players.get(player).addKill();
        updated = true;
    }

    public void addDeath(Player player) {
        if (!players.containsKey(player)) {
            addPlayer(player);
        }
        players.get(player).addDeath();
        updated = true;
    }

    public int getDeaths(Player player) {
        return players.get(player).getDeaths();
    }

    public int getKills(Player player) {
        return players.get(player).getKills();
    }

    public Map<Player, ScoreData> getPlayerScores() {
        return players;
    }

}
