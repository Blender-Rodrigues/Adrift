package ee.taltech.iti0200.di.factory;

import com.google.inject.Inject;
import ee.taltech.iti0200.ai.Goal;
import ee.taltech.iti0200.ai.HealthyBrain;
import ee.taltech.iti0200.ai.LookForPlayer;
import ee.taltech.iti0200.ai.Memory;
import ee.taltech.iti0200.ai.Panic;
import ee.taltech.iti0200.ai.Wander;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.handler.server.BotHurtHandler;
import ee.taltech.iti0200.domain.event.handler.server.BotNoiseHandler;

import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;

public class BotFactory {

    private final World world;
    private final EventBus eventBus;
    private final Random random;

    @Inject
    public BotFactory(World world, EventBus eventBus, Random random) {
        this.world = world;
        this.eventBus = eventBus;
        this.random = random;
    }

    /**
     * Creates a new bot with a gun and a healthy brain
     * subscribes sense handlers to damage and gunshot events
     * adds goals to the brain
     */
    public Bot create() {
        HealthyBrain brain = new HealthyBrain(world);
        Memory memory = new Memory();
        Bot bot = new Bot(world.nextSpawnPoint(), world, brain);
        bot.addWeapon(new Gun(bot.getBoundingBox()));
        bot.setActiveGun(0);

        HashMap<Class<? extends Event>, Subscriber<? extends Event>> subscribers = new HashMap<>();
        subscribers.put(DealDamage.class, new BotHurtHandler(bot));
        subscribers.put(GunShot.class, new BotNoiseHandler(bot));

        Runnable onDeath = () -> subscribers.forEach(eventBus::unsubscribe);

        TreeMap<Long, Goal> goals = new TreeMap<>();
        goals.put(0L, new Wander(bot, world, eventBus, random));
        goals.put(100L, new LookForPlayer(bot, world, eventBus, memory, random));
        goals.put(1500L, new Panic(bot, world, eventBus, random));

        brain.bind(bot, goals, onDeath);

        subscribers.forEach(eventBus::subscribe);

        return bot;
    }

}
