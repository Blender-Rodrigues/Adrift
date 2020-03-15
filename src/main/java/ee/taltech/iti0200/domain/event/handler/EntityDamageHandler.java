package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.event.DealDamage;
import ee.taltech.iti0200.domain.event.Subscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityDamageHandler implements Subscriber<DealDamage> {

    private final Logger logger = LogManager.getLogger(EntityDamageHandler.class);

    private World world;

    public EntityDamageHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(DealDamage event) {
        Damageable target = event.getTarget();
        DamageSource source = event.getSource();

        target.setHealth(target.getHealth() - source.getDamage());
        String action = "hit";

        if (target.getHealth() <= 0) {
            world.removeEntity(target);
            action = "killed";
        }

        String by = source.getOwner() == null ? "" : " by " + source.getOwner();

        logger.info("{} was {}{} with {}", target, action, by, source);
    }

}
