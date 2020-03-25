package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.physics.Vector;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static ee.taltech.iti0200.ai.Sensor.DAMAGE;
import static ee.taltech.iti0200.ai.Sensor.VISUAL;
import static ee.taltech.iti0200.application.Game.eventBus;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

/**
 * Tries to follow and shoot a player
 */
public class LookForPlayer extends Goal {

    public LookForPlayer(Bot bot, World world) {
        super(bot, world);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(RANDOM.nextDouble() - 0.5, 0)); // TODO: add path finding towards most recent player position
        if (bot.canShoot(tick)) {
            lookFor(tick, 0.3, 15, Player.class);
        }
    }

    @Override
    public long react(Sensor sensor, Vector direction, Entity other) {
        if (sensor == VISUAL && other instanceof Player) {
            eventBus.dispatch(new GunShot(bot.getGun(), direction, SERVER));
            return 20;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            // TODO: if the gunshot was close enough, turn head to look towards to see if a player is there
            double distance = bot.getBoundingBox().getCentre().distance(direction);
            return (long) Math.max(20 - distance, 0);
        }

        if (sensor == DAMAGE) {
            return 50;
        }

        return 0;
    }

}
