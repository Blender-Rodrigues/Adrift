package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.physics.Vector;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static ee.taltech.iti0200.ai.Sensor.DAMAGE;
import static ee.taltech.iti0200.ai.Sensor.TACTILE;
import static ee.taltech.iti0200.ai.Sensor.VISUAL;
import static java.lang.Math.max;

/**
 * Mostly harmless unless agitated
 */
public class Wander extends Goal {

    private static final double SIDE_WAYS_MAX = 1 / Math.sqrt(2);
    private static final double SPEED = 0.1;
    private static final double LOOK_ANGLE = 0.2;
    private static final double WANDER_LOOK_INTENSITY = 0.4;
    private static final double MOVING_DIRECTION_LOOK_INTENSITY = 0.2;
    private static final int LOOK_DELAY = 20;
    private static final int GUNSHOT_LOOK_DISTANCE = 30;
    private static final int ADRENALINE_WALL_BUMP = 20;
    private static final int ADRENALINE_GUN_SHOT = 50;
    private static final int ADRENALINE_DAMAGE = 150;
    private static final int ADRENALINE_SPOT_PLAYER = 80;

    private double towards = SPEED;

    public Wander(Bot bot, World world, EventBus eventBus) {
        super(bot, world, eventBus);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(towards, 0));

        Vector newLookingDirection = new Vector(RANDOM.nextDouble() - 0.5, RANDOM.nextDouble() - 0.5);
        Vector movingDirection = new Vector(bot.getSpeed());

        newLookingDirection.normalize();
        movingDirection.normalize();

        bot.lookTowards(newLookingDirection, WANDER_LOOK_INTENSITY);
        bot.lookTowards(movingDirection, MOVING_DIRECTION_LOOK_INTENSITY);

        if (tick % LOOK_DELAY == 0) {
            lookFor(bot.getLookingAt(), LOOK_ANGLE, Player.class);
        }
    }

    @Override
    public long react(long tick, Sensor sensor, Vector location, Vector direction, Entity other) {
        if (sensor == TACTILE && other instanceof Terrain) {
            boolean isSideways = Math.abs(direction.getX()) > SIDE_WAYS_MAX;
            if (!isSideways) {
                return 0;
            }
            towards = direction.getX() > 0 ? -SPEED : SPEED;
            return ADRENALINE_WALL_BUMP;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            double distance = bot.getBoundingBox().getCentre().distance(location);
            if (distance < GUNSHOT_LOOK_DISTANCE) {
                lookFor(direction, LOOK_ANGLE, Player.class);
            }
            return (long) max(ADRENALINE_GUN_SHOT - distance, 0);
        }

        if (sensor == DAMAGE) {
            return ADRENALINE_DAMAGE;
        }

        if (sensor == VISUAL && other instanceof Player) {
            return ADRENALINE_SPOT_PLAYER;
        }

        return 0;
    }

}
