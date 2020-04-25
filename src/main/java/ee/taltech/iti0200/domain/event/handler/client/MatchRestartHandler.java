package ee.taltech.iti0200.domain.event.handler.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.RecreateException;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.GameWon;
import ee.taltech.iti0200.domain.event.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class MatchRestartHandler implements Subscriber<GameWon> {

    private static final int RESTART_DELAY = 2000;

    private final Logger logger = LogManager.getLogger(MatchRestartHandler.class);
    private final World world;

    @Inject
    public MatchRestartHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(GameWon event) {
        UUID id = event.getId();
        Player player = (Player) world.getEntity(id);

        if (player == null) {
            logger.warn("Game won by {}", id);
        } else {
            logger.info("Game won by {}", player);
        }

        throw new RecreateException(RESTART_DELAY);
    }

}
