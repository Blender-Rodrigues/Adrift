package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.physics.Vector;

import static ee.taltech.iti0200.ai.Sensor.AUDIO;
import static ee.taltech.iti0200.ai.Sensor.DAMAGE;
import static ee.taltech.iti0200.ai.Sensor.TACTILE;
import static ee.taltech.iti0200.ai.Sensor.VISUAL;

/**
 * Mostly harmless unless agitated
 */
public class Wander extends Goal {

    private static final double SIDE_WAYS_MAX = 1 / Math.sqrt(2);
    private static final double SPEED = 0.1;

    private double towards = SPEED;

    public Wander(Bot bot, World world) {
        super(bot, world);
    }

    @Override
    public void execute(long tick) {
        move(new Vector(towards, 0));
        lookFor(tick, 0.2, 20, Player.class);
    }

    @Override
    public long react(Sensor sensor, Vector direction, Entity other) {
        if (sensor == TACTILE && other instanceof Terrain) {
            boolean isSideways = Math.abs(direction.getX()) > SIDE_WAYS_MAX;
            if (!isSideways) {
                return 0;
            }
            towards = direction.getX() > 0 ? -SPEED : SPEED;
            return 20;
        }

        if (sensor == AUDIO && other instanceof Gun) {
            // TODO: if the gunshot was close enough, turn head to look towards to see if a player is there
            double distance = bot.getBoundingBox().getCentre().distance(direction);
            return (long) Math.max(50 - distance, 0);
        }

        if (sensor == DAMAGE) {
            return 150;
        }

        if (sensor == VISUAL && other instanceof Player) {
            return 80;
        }

        return 0;
    }

}
