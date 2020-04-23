package ee.taltech.iti0200.domain.event.handler.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class GunShotHandler implements Subscriber<GunShot> {

    private final Logger logger = LogManager.getLogger(GunShotHandler.class);

    private final World world;
    private final EventBus eventBus;

    @Inject
    public GunShotHandler(World world, EventBus eventBus) {
        this.world = world;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(GunShot event) {
        Gun gun = event.getGun();
        if (!canShoot(gun)) {
            event.stop();
            return;
        }

        Projectile projectile = gun.shoot(event.getDirection(), world.getTime());

        logger.debug("{} shot by {}", projectile, projectile.getOwner());

        eventBus.dispatch(new CreateEntity(projectile, EVERYONE));
    }

    private boolean canShoot(Gun gun) {
        Living owner = gun.getOwner();
        if (owner == null) {
            logger.warn("Owner of a gun {} is missing during a shot", gun);
            return false;
        }

        Living local = (Living) world.getEntity(owner.getId());
        if (local == null) {
            logger.warn("Owner {} of a gun is not present in the world during a shot", local);
            return false;
        }

        Gun serverGun = local.getActiveGun();
        if (!serverGun.getId().equals(gun.getId())) {
            logger.debug("Gun has been switched on the server side");
            return false;
        }

        return serverGun.canShoot(world.getTime());
    }

}
