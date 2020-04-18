package ee.taltech.iti0200.domain.event.common;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.ChangeEquipment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Currently only changing active gun
 */
public class ChangeEquipmentHandler implements Subscriber<ChangeEquipment> {

    private final Logger logger = LogManager.getLogger(ChangeEquipmentHandler.class);

    final private World world;

    @Inject
    public ChangeEquipmentHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(ChangeEquipment event) {
        Player player = (Player) world.getEntity(event.getPlayerId());
        if (player == null) {
            logger.warn("Player {} not in the world", event.getPlayerId());
            return;
        }

        player.setActiveGun(event.getSlot());
    }

}
