package ee.taltech.iti0200.domain.event.handler.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.ai.Sensor;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Loot;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.AddGun;
import ee.taltech.iti0200.domain.event.handler.common.CollisionHandler;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.Heal;
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

        if (
            event.getEntity() instanceof Loot && event.getOther() instanceof Player
            || event.getEntity() instanceof HealthGlobe && event.getOther() instanceof Living
        ) {
            livingHitLoot((Living) event.getOther(), (Loot) event.getEntity());
        }
        event.stop();
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

    private void livingHitLoot(Living living, Loot loot) {
        if (loot instanceof HealthGlobe) {
            eventBus.dispatch(new Heal((HealthGlobe) loot, living, EVERYONE));
        } else if (loot instanceof Gun) {
            eventBus.dispatch(new AddGun((Gun) loot, living, EVERYONE));
        }

        eventBus.dispatch(new RemoveEntity(loot, EVERYONE));
    }

}
