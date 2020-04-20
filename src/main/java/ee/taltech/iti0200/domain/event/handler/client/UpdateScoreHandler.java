package ee.taltech.iti0200.domain.event.handler.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.ScoreData;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.UpdateScore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class UpdateScoreHandler implements Subscriber<UpdateScore> {

    private final Logger logger = LogManager.getLogger(UpdateScoreHandler.class);
    private final World world;
    private final Score score;

    @Inject
    public UpdateScoreHandler(World world, Score score) {
        this.world = world;
        this.score = score;
    }

    @Override
    public void handle(UpdateScore event) {
        Map<Player, ScoreData> localScores = score.getPlayerScores();

        event.getScores().forEach((id, scoreData) -> {
            Player player = (Player) world.getEntity(id);
            if (player == null) {
                logger.trace("Player {} not in the world", id);
                return;
            }

            ScoreData data = localScores.getOrDefault(player, new ScoreData());
            data.setKills(scoreData[0]);
            data.setDeaths(scoreData[1]);
            localScores.put(player, data);
        });

        score.setUpdated(true);
        logger.debug("Updated {} scores from server", event.getScores().size());
    }

}
