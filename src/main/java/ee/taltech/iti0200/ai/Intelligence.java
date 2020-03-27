package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Intelligence implements Component {

    private List<Bot> bots = new ArrayList<>();
    private World world;

    public Intelligence(World world) {
        this.world = world;
    }

    @Override
    public void initialize() {
        bots.add(createBot());
        bots.add(createBot());

        bots.forEach(world::addEntity);
    }

    @Override
    public void update(long tick) {
        if (tick % 10 != 0) {
            return;
        }

        Iterator<Bot> iterator = bots.iterator();
        while (iterator.hasNext()) {
            Bot bot = iterator.next();
            if (bot.isAlive()) {
                bot.update(tick);
            } else {
                iterator.remove();
            }
        }
    }

    private Bot createBot() {
        HealthyBrain brain = new HealthyBrain(world);
        Bot bot = new Bot(world.nextPlayerSpawnPoint(), world, brain);
        brain.bind(bot);
        return bot;
    }

}
