package ee.taltech.iti0200.domain.event.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Gun;
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

    private World world;
    private EventBus eventBus;

    @Inject
    public GunShotHandler(World world, EventBus eventBus) {
        this.world = world;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(GunShot event) {
        Gun gun = event.getGun();
        if (!gun.canShoot(world.getTime())) {
            event.stop();
            return;
        }

        Projectile projectile = gun.shoot(event.getDirection(), world.getTime());

        logger.debug("{} shot by {}", projectile, projectile.getOwner());

        eventBus.dispatch(new CreateEntity(projectile, EVERYONE));
    }

}
