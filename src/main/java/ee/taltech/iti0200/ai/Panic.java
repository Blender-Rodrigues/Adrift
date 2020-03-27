package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Living;
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

    private static final double LOOK_ANGLE = 0.3;
    private static final int LOOK_DELAY = 10;
    private static final int GUNSHOT_LOOK_DISTANCE = 40;
    private static final int GUNSHOT_TRIGGER_HAPPY_DISTANCE = 30;
    private static final int ADRENALINE_GUN_SHOT = 80;
    private static final int ADRENALINE_DAMAGE = 100;
    private static final int ADRENALINE_SPOT_LIVING = 40;

    public Panic(Bot bot, World world) {
        super(bot, world);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(RANDOM.nextDouble() - 0.5, 0));
        if (tick % LOOK_DELAY == 0) {
            lookFor(bot.getSpeed(), LOOK_ANGLE, Living.class);
        }
    }

    @Override
    public long react(long tick, Sensor sensor, Vector location, Vector direction, Entity other) {
        if (sensor == VISUAL && other instanceof Living) {
            if (bot.canShoot(tick)) {
                eventBus.dispatch(new GunShot(bot.getGun(), direction, SERVER));
            }
            return ADRENALINE_SPOT_LIVING;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            double distance = bot.getBoundingBox().getCentre().distance(location);

            if (distance < GUNSHOT_TRIGGER_HAPPY_DISTANCE) {
                if (RANDOM.nextBoolean() && bot.canShoot(tick)) {
                    eventBus.dispatch(new GunShot(bot.getGun(), direction, SERVER));
                }
            } else if (distance < GUNSHOT_LOOK_DISTANCE) {
                lookFor(direction, LOOK_ANGLE, Living.class);
            }

            return (long) Math.max(ADRENALINE_GUN_SHOT - distance, 0);
        }

        if (sensor == DAMAGE) {
            return ADRENALINE_DAMAGE;
        }

        return 0;
    }

}
