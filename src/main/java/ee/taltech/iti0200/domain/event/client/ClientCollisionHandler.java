package ee.taltech.iti0200.domain.event.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.GameId;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.common.CollisionHandler;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.network.message.Receiver;

import java.util.UUID;

public class ClientCollisionHandler extends CollisionHandler {

    private final EventBus eventBus;
    private final UUID id;

    @Inject
    public ClientCollisionHandler(World world, EventBus eventBus, @GameId UUID id) {
        super(world);
        this.eventBus = eventBus;
        this.id = id;
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
    }

    /**
     * Removing projectile from client side first to make the visual smoother
     */
    private void projectileHitAny(Projectile projectile, Entity other) {
        if (other.equals(projectile.getOwner())) {
            return;
        }

        eventBus.dispatch(new RemoveEntity(projectile, new Receiver(id)));
    }

}
