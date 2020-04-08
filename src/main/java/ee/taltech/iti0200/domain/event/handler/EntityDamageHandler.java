package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class EntityDamageHandler implements Subscriber<DealDamage> {

    private final Logger logger = LogManager.getLogger(EntityDamageHandler.class);

    private World world;
    private Score score;
    private EventBus eventBus;

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
            eventBus.dispatch(new RemoveEntity(target, EVERYONE));
            action = "killed";
            if (target instanceof Player) {
                score.addDeath((Player) target);
            }
            if (source.getOwner() != null && source.getOwner() instanceof Player && target instanceof Living) {
                score.addKill((Player) source.getOwner());
            }
        }

        String by = source.getOwner() == null ? "" : " by " + source.getOwner();

        logger.info("{} was {}{} with {}", target, action, by, source);
    }

    private Damageable loadLocal(Entity entity) {
        return (Damageable) world.getEntity(entity.getId());
    }

}
