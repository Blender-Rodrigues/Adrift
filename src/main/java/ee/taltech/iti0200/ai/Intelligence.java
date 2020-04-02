package ee.taltech.iti0200.ai;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.di.factory.BotFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Intelligence implements Component {

    private List<Bot> bots = new ArrayList<>();
    private World world;
    private BotFactory botFactory;

    @Inject
    public Intelligence(World world, BotFactory botFactory) {
        this.world = world;
        this.botFactory = botFactory;
    }

    @Override
    public void initialize() {
        bots.add(botFactory.create());
        bots.add(botFactory.create());

        bots.forEach(world::addEntity);
    }

    @Override
    public void update(long tick) {
        if (tick % 10 != 0) {
            return;
        }

        // Condition is same as botAmount < playerAmount + 1
        while (2 * bots.size() < world.getLivingEntities().size() + 1) {
            Bot bot = botFactory.create();
            bots.add(bot);
            world.addEntity(bot);
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

}
