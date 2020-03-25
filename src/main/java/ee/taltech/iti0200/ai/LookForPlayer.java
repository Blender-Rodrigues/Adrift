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

    private static final double LOOK_ANGLE = 0.2;
    private static final int LOOK_DELAY = 15;
    private static final int GUNSHOT_LOOK_DISTANCE = 20;
    private static final int ADRENALINE_GUN_SHOT = 20;
    private static final int ADRENALINE_DAMAGE = 50;
    private static final int ADRENALINE_SPOT_PLAYER = 20;

    public LookForPlayer(Bot bot, World world) {
        super(bot, world);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(RANDOM.nextDouble() - 0.5, 0));
        if (bot.canShoot(tick) && tick % LOOK_DELAY == 0) {
            lookFor(bot.getSpeed(), LOOK_ANGLE, Player.class);
        }
    }

    @Override
    public long react(long tick, Sensor sensor, Vector location, Vector direction, Entity other) {
        if (sensor == VISUAL && other instanceof Player) {
            if (bot.canShoot(tick)) {
                eventBus.dispatch(new GunShot(bot.getGun(), direction, SERVER));
            }
            return ADRENALINE_SPOT_PLAYER;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            double distance = bot.getBoundingBox().getCentre().distance(location);
            if (distance < GUNSHOT_LOOK_DISTANCE) {
                lookFor(direction, LOOK_ANGLE, Player.class);
            }
            return (long) Math.max(ADRENALINE_GUN_SHOT - distance, 0);
        }

        if (sensor == DAMAGE) {
            return ADRENALINE_DAMAGE;
        }

        return 0;
    }

}
