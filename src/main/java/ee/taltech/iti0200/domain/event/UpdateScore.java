package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.domain.ScoreData;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class UpdateScore extends Event implements Message {

    private static final long serialVersionUID = 1L;
    private final Map<UUID, Integer[]> scores = new HashMap<>();

    public UpdateScore(Map<Player, ScoreData> scores, Receiver receiver) {
        super(receiver);
        scores.forEach((player, data) -> this.scores.put(
            player.getId(),
            new Integer[]{data.getKills(), data.getDeaths()}
        ));
    }

    public Map<UUID, Integer[]> getScores() {
        return scores;
    }

    @Override
    public String toString() {
        String summary = scores.entrySet()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue()[0] + "/" + entry.getValue()[0])
            .collect(Collectors.joining(", "));
        return format("UpdateScore{%s}", summary);
    }

}
