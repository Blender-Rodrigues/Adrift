package ee.taltech.iti0200.domain.event.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.GunShot;

public class ClientGunShotHandler implements Subscriber<GunShot> {

    private final World world;

    @Inject
    public ClientGunShotHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(GunShot event) {
        Gun gun = event.getGun();
        long tick = world.getTime();

        if (!gun.canShoot(tick)) {
            event.stop();
            return;
        }

        gun.resetCooldown(tick);
    }

}
