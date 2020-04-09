package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.HealingSource;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.HealDamage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityHealingHandler implements Subscriber<HealDamage> {

    private final Logger logger = LogManager.getLogger(EntityHealingHandler.class);

    private World world;

    @Inject
    public EntityHealingHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(HealDamage event) {
        Damageable target = loadLocal(event.getTarget());
        if (target == null) {
            logger.debug("Target {} does not exist in world", event.getTarget());
            return;
        }

        HealingSource source = event.getSource();

        target.setHealth(target.getHealth() + source.getHealing());
        logger.info("{} was healed with {}", target, source);
    }

    private Damageable loadLocal(Entity entity) {
        return (Damageable) world.getEntity(entity.getId());
    }
}
