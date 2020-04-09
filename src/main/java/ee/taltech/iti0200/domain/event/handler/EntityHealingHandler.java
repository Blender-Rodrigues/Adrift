package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.HealingSource;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.Heal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityHealingHandler implements Subscriber<Heal> {

    private final Logger logger = LogManager.getLogger(EntityHealingHandler.class);

    private World world;

    @Inject
    public EntityHealingHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(Heal event) {
        Damageable target = loadLocal(event.getTarget());
        if (target == null) {
            logger.debug("Target {} does not exist in world", event.getTarget());
            return;
        }

        HealingSource source = event.getSource();

        target.setHealth(
            Math.min(
                target.getHealth() + source.getHealing(),
                target.getMaxHealth()
            )
        );

        logger.info("{} was healed with {}", target, source);
    }

    private Damageable loadLocal(Entity entity) {
        return (Damageable) world.getEntity(entity.getId());
    }
}
