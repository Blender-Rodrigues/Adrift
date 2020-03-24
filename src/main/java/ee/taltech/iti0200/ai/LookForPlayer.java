package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.physics.Vector;

import javax.vecmath.Vector2d;
import java.util.Comparator;
import java.util.Optional;

import static ee.taltech.iti0200.application.Game.eventBus;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

public class LookForPlayer extends Goal {

    public LookForPlayer(Bot bot, World world) {
        super(bot, world);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(RANDOM.nextDouble() - 0.5, 0));
        if (bot.canShoot(tick)) {
            lookForPlayer().ifPresent(target -> eventBus.dispatch(new GunShot(bot.getGun(), target, SERVER)));
        }
    }

    @Override
    public void react(Sensor sensor, Vector direction, Entity other) {

    }

    private Optional<Vector> lookForPlayer() {
        return world.getLivingEntities().stream()
            .filter(Player.class::isInstance)
            .map(player -> {
                Vector vector = new Vector(player.getBoundingBox().getCentre());
                vector.sub(bot.getBoundingBox().getCentre());
                return vector;
            })
            .filter(vector -> vector.angle(bot.getSpeed()) < 0.2)
            .min(Comparator.comparing(Vector2d::lengthSquared));
    }

}
