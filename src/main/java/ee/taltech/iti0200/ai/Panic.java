package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.physics.Vector;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static ee.taltech.iti0200.ai.Sensor.DAMAGE;
import static ee.taltech.iti0200.ai.Sensor.VISUAL;
import static ee.taltech.iti0200.application.Game.eventBus;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

/**
 * Starts to shoot more often and more randomly
 */
public class Panic extends Goal {

    public Panic(Bot bot, World world) {
        super(bot, world);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(RANDOM.nextDouble() - 0.5, 0));
        lookFor(tick, 0.6, 10, Living.class);
    }

    @Override
    public long react(Sensor sensor, Vector direction, Entity other) {
        if (sensor == VISUAL && other instanceof Living) {
            eventBus.dispatch(new GunShot(bot.getGun(), direction, SERVER));
            return 40;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            // TODO: if the gunshot was close enough, turn head to look towards to see if anyone is there
            double distance = bot.getBoundingBox().getCentre().distance(direction);
            return (long) Math.max(80 - distance, 0);
        }

        if (sensor == DAMAGE) {
            return 100;
        }

        return 0;
    }

}
