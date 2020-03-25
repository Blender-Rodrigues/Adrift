package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.handler.BotHurtHandler;
import ee.taltech.iti0200.domain.event.handler.BotNoiseHandler;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.TreeMap;

import static ee.taltech.iti0200.application.Game.eventBus;
import static java.lang.String.format;

public class HealthyBrain implements Brain {

    private final Logger logger = LogManager.getLogger(HealthyBrain.class);

    private Bot bot;
    private World world;
    private Goal active;
    private long adrenaline = 0;
    private TreeMap<Long, Goal> goals = new TreeMap<>();
    protected HashMap<Class<? extends Event>, Subscriber<? extends Event>> subscribers = new HashMap<>();

    public HealthyBrain(World world) {
        this.world = world;
    }

    public void bind(Bot bot) {
        this.bot = bot;
        goals.put(0L, new Wander(bot, world));
        goals.put(100L, new LookForPlayer(bot, world));
        goals.put(1000L, new Panic(bot, world));

        subscribers.put(DealDamage.class, new BotHurtHandler(bot));
        subscribers.put(GunShot.class, new BotNoiseHandler(bot));

        subscribers.forEach((key, value) -> eventBus.subscribe(key, value));
    }

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
    }

    @Override
    public void kill() {
        subscribers.forEach((key, value) -> eventBus.unsubscribe(key, value));
    }

    @Override
    public String toString() {
        return format("Brain{bot=%s, active=%s, adrenaline=%d}", bot, active, adrenaline);
    }

}
