package ee.taltech.iti0200.domain.event.handler.common;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.AddGun;
import ee.taltech.iti0200.graphics.renderer.EntityRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityGiveGunHandler implements Subscriber<AddGun> {

    private final Logger logger = LogManager.getLogger(EntityGiveGunHandler.class);

    private World world;

    @Inject
    public EntityGiveGunHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(AddGun event) {
        Living target = loadLocal(event.getTarget());
        if (target == null) {
            logger.trace("Target {} does not exist in world", event.getTarget());
            return;
        }

        Gun gun = event.getGun();

        if (!target.hasGun(gun)) {
            target.addWeapon(gun);
            ((EntityRenderer) gun.getRenderer()).reScale();
            logger.info("{} was given {}", target, gun);
            return;
        }

        logger.info("{} already had {} and was not given a second one", target, gun);

    }

    private Living loadLocal(Entity entity) {
        return (Living) world.getEntity(entity.getId());
    }
}
