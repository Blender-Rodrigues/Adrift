package ee.taltech.iti0200.ai;

import com.google.common.collect.Maps;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.physics.Vector;

import java.util.Comparator;
import java.util.Random;

public abstract class Goal {

    protected static final Random RANDOM = new Random();

    protected Bot bot;
    protected World world;

    public Goal(Bot bot, World world) {
        this.bot = bot;
        this.world = world;
    }

    abstract public void execute(long tick);

    abstract long react(Sensor sensor, Vector direction, Entity other);

    protected void move(Vector towards) {
        Vector acceleration = bot.getAcceleration();

        acceleration.add(towards);
        acceleration.scale(0.9);

        Vector speed = bot.getSpeed();
        speed.add(acceleration);
    }

    /**
     * TODO: filter out targets who are hidden behind a block
     * lookAngle is in radians
     */
    protected void lookFor(long tick, double lookAngle, int lookDelay, Class<? extends Entity> type) {
        if (tick % lookDelay != 0) {
            return;
        }

        world.getLivingEntities().stream()
            .filter(type::isInstance)
            .map(player -> {
                Vector vector = new Vector(player.getBoundingBox().getCentre());
                vector.sub(bot.getBoundingBox().getCentre());
                return Maps.immutableEntry(vector, (Player) player);
            })
            .filter(entry -> entry.getKey().angle(bot.getSpeed()) < lookAngle)
            .min(Comparator.comparingDouble(a -> a.getKey().lengthSquared()))
            .ifPresent(
                target -> bot.getBrain().updateSensor(Sensor.VISUAL, target.getKey(), target.getValue())
            );
    }

    @Override
    public String toString() {
        return "Goal{" + getClass().getSimpleName() + "}";
    }

}
