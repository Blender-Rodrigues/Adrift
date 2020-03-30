package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.ai.Sensor;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class ServerCollisionHandler extends CollisionHandler {

    private EventBus eventBus;

    @Inject
    public ServerCollisionHandler(World world, EventBus eventBus) {
        super(world);
        this.eventBus = eventBus;
    }

    @Override
    public void handle(EntityCollide event) {
        super.handle(event);
        if (event.isStopped()) {
            return;
        }

        if (event.getEntity() instanceof Projectile) {
            projectileHitAny((Projectile) event.getEntity(), event.getOther());
        }
        if (event.getEntity() instanceof Bot) {
            botHitAny((Bot) event.getEntity(), event.getOther());
        }

    }

    private void projectileHitAny(Projectile projectile, Entity other) {
        if (other.equals(projectile.getOwner())) {
            return;
        }

        if (other instanceof Damageable) {
            eventBus.dispatch(new DealDamage(projectile, (Damageable) other, EVERYONE));
        }

        eventBus.dispatch(new RemoveEntity(projectile, EVERYONE));
    }

    private void botHitAny(Bot bot, Entity other) {
        bot.getBrain().updateSensor(Sensor.TACTILE, other.getBoundingBox().getCentre(), other);
    }

}
