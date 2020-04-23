package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.physics.Vector;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static ee.taltech.iti0200.ai.Sensor.DAMAGE;
import static ee.taltech.iti0200.ai.Sensor.VISUAL;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

/**
 * Starts to shoot more often and more randomly
 */
public class Panic extends Goal {

    private static final double LOOK_ANGLE = 0.3;
    private static final Vector SPEED = new Vector(1, 0.3);
    private static final double JUMP_SPEED = 9;
    private static final int JUMP_DELAY = 40;
    private static final int GUNSHOT_LOOK_DISTANCE = 40;
    private static final int GUNSHOT_TRIGGER_HAPPY_DISTANCE = 30;
    private static final int ADRENALINE_GUN_SHOT = 80;
    private static final int ADRENALINE_DAMAGE = 100;
    private static final int ADRENALINE_SPOT_LIVING = 40;
    public static final int Y_LIMIT_FOR_JUMP = 3;

    private Vector danger;

    public Panic(Bot bot, World world, EventBus eventBus) {
        super(bot, world, eventBus);
        danger = new Vector();
    }

    @Override
    public void execute(long tick) {
        Vector dangerDirection = new Vector(danger);
        dangerDirection.sub(bot.getBoundingBox().getCentre());

        if (dangerDirection.getY() < -Y_LIMIT_FOR_JUMP && tick % JUMP_DELAY == 0) {
            bot.accelerate(new Vector(0, JUMP_SPEED));
        }

        dangerDirection.normalize();
        if (bot.canShoot(tick)) {
            eventBus.dispatch(new GunShot(bot.getActiveGun(), dangerDirection, SERVER));
        }
        Vector moveDirection = new Vector(dangerDirection);
        moveDirection.scale(-1);
        moveDirection.elementWiseMultiple(SPEED);
        move(moveDirection);
    }

    @Override
    public long react(long tick, Sensor sensor, Vector location, Vector direction, Entity other) {
        if (sensor == VISUAL && other instanceof Living) {
            if (bot.canShoot(tick)) {
                eventBus.dispatch(new GunShot(bot.getActiveGun(), direction, SERVER));
            }
            danger = location;
            return ADRENALINE_SPOT_LIVING;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            double distance = bot.getBoundingBox().getCentre().distance(location);

            if (distance < GUNSHOT_TRIGGER_HAPPY_DISTANCE) {
                if (RANDOM.nextBoolean() && bot.canShoot(tick)) {
                    eventBus.dispatch(new GunShot(bot.getActiveGun(), direction, SERVER));
                }
            } else if (distance < GUNSHOT_LOOK_DISTANCE) {
                lookFor(direction, LOOK_ANGLE, Living.class);
            }

            danger = location;

            return (long) Math.max(ADRENALINE_GUN_SHOT - distance, 0);
        }

        if (sensor == DAMAGE) {
            return ADRENALINE_DAMAGE;
        }

        return 0;
    }


}
