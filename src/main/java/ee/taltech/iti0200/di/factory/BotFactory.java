package ee.taltech.iti0200.di.factory;

import com.google.inject.Inject;
import ee.taltech.iti0200.ai.Goal;
import ee.taltech.iti0200.ai.HealthyBrain;
import ee.taltech.iti0200.ai.LookForPlayer;
import ee.taltech.iti0200.ai.Panic;
import ee.taltech.iti0200.ai.Wander;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.handler.BotHurtHandler;
import ee.taltech.iti0200.domain.event.handler.BotNoiseHandler;

import java.util.HashMap;
import java.util.TreeMap;

public class BotFactory {

    private World world;
    private EventBus eventBus;

    @Inject
    public BotFactory(World world, EventBus eventBus) {
        this.world = world;
        this.eventBus = eventBus;
    }

    /**
     * Creates a new bot with a gun and a healthy brain
     * subscribes sense handlers to damage and gunshot events
     * adds goals to the brain
     */
    public Bot create() {
        HealthyBrain brain = new HealthyBrain(world);
        Bot bot = new Bot(world.nextPlayerSpawnPoint(), world, brain);
        bot.setGun(new Gun(bot.getBoundingBox()));

        HashMap<Class<? extends Event>, Subscriber<? extends Event>> subscribers = new HashMap<>();
        subscribers.put(DealDamage.class, new BotHurtHandler(bot));
        subscribers.put(GunShot.class, new BotNoiseHandler(bot));
        subscribers.forEach((key, value) -> eventBus.subscribe(key, value));
        Runnable onDeath = () -> subscribers.forEach((key, value) -> eventBus.unsubscribe(key, value));

        TreeMap<Long, Goal> goals = new TreeMap<>();
        goals.put(0L, new Wander(bot, world, eventBus));
        goals.put(100L, new LookForPlayer(bot, world, eventBus));
        goals.put(1000L, new Panic(bot, world, eventBus));

        brain.bind(bot, goals, onDeath);

        return bot;
    }

}