package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityDamageHandler implements Subscriber<DealDamage> {

    private final Logger logger = LogManager.getLogger(EntityDamageHandler.class);

    private World world;

    @Inject
    public EntityDamageHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(DealDamage event) {
        Damageable target = loadLocal(event.getTarget());
        if (target == null) {
            logger.debug("Target {} does not exist in world", event.getTarget());
            return;
        }

        DamageSource source = event.getSource();

        if (target.equals(source.getOwner())) {
            event.stop();
            return;
        }

        target.setHealth(target.getHealth() - source.getDamage());
        String action = "hit";

        if (target.getHealth() <= 0) {
            world.removeEntity(target);
            action = "killed";
        }

        String by = source.getOwner() == null ? "" : " by " + source.getOwner();

        logger.info("{} was {}{} with {}", target, action, by, source);
    }

    private Damageable loadLocal(Entity entity) {
        return (Damageable) world.getEntity(entity.getId());
    }

}
