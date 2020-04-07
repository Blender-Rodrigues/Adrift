package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.domain.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Score {

    private Map<Player, ScoreData> players;

    public Score() {
        players = new HashMap<>();
    }

    public void addPlayer(Player player) {
        players.put(player, new ScoreData());
    }

    public void addKill(Player player) {
        if (!players.containsKey(player)) {
            addPlayer(player);
        }
        System.out.println("killing");
        players.get(player).addKill();
    }

    public void addDeath(Player player) {
        if (!players.containsKey(player)) {
            addPlayer(player);
        }
        System.out.println("dieing");
        players.get(player).addDeath();
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
