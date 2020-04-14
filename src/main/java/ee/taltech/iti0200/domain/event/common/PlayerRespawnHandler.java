package ee.taltech.iti0200.domain.event.common;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.RespawnPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerRespawnHandler implements Subscriber<RespawnPlayer> {

    private final Logger logger = LogManager.getLogger(PlayerRespawnHandler.class);
    private final World world;

    @Inject
    public PlayerRespawnHandler(World world) {
        this.world = world;
    }

    public void handle(RespawnPlayer event) {
        Player player = (Player) world.getEntity(event.getId());
        if (player == null) {
            logger.error("Player {} not in the world", event.getId());
            return;
        }

        logger.info(
            "Re-spawning {} with {} lives, moving from {} to {}",
            player,
            event.getLives(),
            player.getBoundingBox().getCentre(),
            event.getPosition()
        );

        player.setHealth(player.getMaxHealth());
        player.setLives(event.getLives());
        player.setPosition(event.getPosition());
    }

}
