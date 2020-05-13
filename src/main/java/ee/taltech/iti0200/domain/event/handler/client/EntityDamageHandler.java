package ee.taltech.iti0200.domain.event.handler.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityDamageHandler implements Subscriber<DealDamage> {

    private final Logger logger = LogManager.getLogger(EntityDamageHandler.class);

    protected World world;
    protected Score score;
    protected EventBus eventBus;

    @Inject
    public EntityDamageHandler(World world, Score score, EventBus eventBus) {
        this.world = world;
        this.score = score;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(DealDamage event) {
        Damageable target = loadLocal(event.getTarget());
        if (target == null) {
            logger.trace("Target {} does not exist in world", event.getTarget());
            event.stop();
            return;
        }

        DamageSource source = event.getSource();
        if (target.equals(source.getOwner())) {
            event.stop();
            return;
        }

        int shieldPostDamage = target.getShield() - source.getDamage();
        if (shieldPostDamage >= 0) {
            target.setShield(shieldPostDamage);
        } else {
            target.setShield(0);
            target.setHealth(target.getHealth() + shieldPostDamage);
        }
        String action = "hit";

        if (target.getHealth() <= 0) {
            target.setHealth(0);
            action = "killed";
            fatal(target, source);
        }

        String by = source.getOwner() == null ? "" : " by " + source.getOwner();

        logger.info("{} now at {} was {}{} with {}", target, target.getHealth(), action, by, source);
    }

    protected void fatal(Damageable target, DamageSource source) {

    }

    private Damageable loadLocal(Entity entity) {
        return (Damageable) world.getEntity(entity.getId());
    }

}
