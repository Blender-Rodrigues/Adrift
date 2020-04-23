package ee.taltech.iti0200.ai;

import com.google.common.collect.Maps;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.physics.Vector;

import java.util.Comparator;
import java.util.Map;
import java.util.Random;

import static ee.taltech.iti0200.domain.entity.Terrain.TERRAIN_BLOCK_RESOLUTION;
import static ee.taltech.iti0200.domain.entity.Terrain.TERRAIN_BLOCK_SIZE;

public abstract class Goal {

    private static final double TERRAIN_Y_OFFSET = TERRAIN_BLOCK_SIZE / 2;

    protected Bot bot;
    protected World world;
    protected EventBus eventBus;
    protected Random random;

    public Goal(Bot bot, World world, EventBus eventBus, Random random) {
        this.bot = bot;
        this.world = world;
        this.eventBus = eventBus;
        this.random = random;
    }

    abstract public void execute(long tick);

    abstract long react(long tick, Sensor sensor, Vector location, Vector direction, Entity other);

    protected void move(Vector towards) {
        Vector acceleration = bot.getAcceleration();

        acceleration.add(towards);
        acceleration.scale(0.9);

        Vector speed = bot.getSpeed();
        speed.add(acceleration);
        bot.setSpeed(speed);
    }

    /**
     * lookAngle is in radians
     */
    protected void lookFor(
        Vector lookDirection,
        double lookAngle,
        Class<? extends Entity> type
    ) {
        world.getLivingEntities().stream()
            .filter(type::isInstance)
            .filter(entity -> !bot.equals(entity))
            .map(player -> {
                Vector vector = new Vector(player.getBoundingBox().getCentre());
                vector.sub(bot.getBoundingBox().getCentre());
                return Maps.immutableEntry(vector, type.cast(player));
            })
            .filter(entry -> entry.getKey().angle(lookDirection) < lookAngle)
            .min(Comparator.comparingDouble(a -> a.getKey().lengthSquared()))
            .filter(this::visible)
            .ifPresent(
                target -> bot.getBrain().updateSensor(
                    Sensor.VISUAL,
                    target.getValue().getBoundingBox().getCentre(),
                    target.getValue()
                )
            );
    }

    @Override
    public String toString() {
        return "Goal{" + getClass().getSimpleName() + "}";
    }

    /**
     * From the position of the bot start looking towards the target to see
     * if the bot has a clear line of sight or if there is a terrain block in between.
     */
    public boolean visible(Vector target, Vector direction) {
        direction.normalize();

        Vector centre = new Vector(bot.getBoundingBox().getCentre());

        Map<Vector, Terrain> terrain = world.getTerrainMap();



        while (shouldContinue(direction, centre, target)) {
            centre.add(direction);

            double x = Math.round(centre.x * TERRAIN_BLOCK_RESOLUTION) / TERRAIN_BLOCK_RESOLUTION;
            double y = Math.round(centre.y) + TERRAIN_Y_OFFSET;

            Vector key = new Vector(x, y);
            if (terrain.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    public boolean visible(Vector target) {
        Vector direction = new Vector(target);
        direction.sub(bot.getBoundingBox().getCentre());
        return visible(target, direction);
    }

    public boolean visible(Map.Entry<Vector, ? extends Entity> entry) {
        return visible(entry.getValue().getBoundingBox().getCentre(), entry.getKey());
    }

    private boolean shouldContinue(Vector direction, Vector centre, Vector target) {
        if (direction.x > 0 && centre.x >= target.x) {
            return false;
        }
        if (direction.y > 0 && centre.y >= target.y) {
            return false;
        }
        if (direction.x < 0 && centre.x < target.x) {
            return false;
        }
        if (direction.y < 0 && centre.y < target.y) {
            return false;
        }
        return true;
    }

}
