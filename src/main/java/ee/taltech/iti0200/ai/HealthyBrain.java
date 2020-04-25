package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TreeMap;

import static java.lang.String.format;

public class HealthyBrain implements Brain {

    private final Logger logger = LogManager.getLogger(HealthyBrain.class);

    private Bot bot;
    private World world;
    private Runnable onDeath;
    private Goal active;
    private long adrenaline = 0;
    private TreeMap<Long, Goal> goals = new TreeMap<>();

    public HealthyBrain(World world) {
        this.world = world;
    }

    @Override
    public void bind(Bot bot, TreeMap<Long, Goal> goals, Runnable onDeath) {
        this.bot = bot;
        this.goals = goals;
        this.onDeath = onDeath;
        this.active = goals.floorEntry(adrenaline).getValue();
    }

    @Override
    public void followGoal(long tick) {
        if (!bot.isAlive()) {
            return;
        }
        if (tick % 5 == 0 && adrenaline > 0) {
            adrenaline -= 1;
        }
        active = goals.floorEntry(adrenaline).getValue();
        active.execute(tick);
    }

    @Override
    public void updateSensor(Sensor sensor, Vector location, Entity other) {
        if (!bot.isAlive()) {
            return;
        }

        logger.trace(this);

        Vector direction = new Vector(location);
        direction.sub(bot.getBoundingBox().getCentre());
        direction.normalize();

        adrenaline += active.react(world.getTime(), sensor, new Vector(location), direction, other);
        if (adrenaline < 0) {
            adrenaline = 0;
        }
        System.out.println(adrenaline);
    }

    @Override
    public void kill() {
        onDeath.run();
    }

    @Override
    public String toString() {
        return format("Brain{bot=%s, active=%s, adrenaline=%d}", bot, active, adrenaline);
    }

}
