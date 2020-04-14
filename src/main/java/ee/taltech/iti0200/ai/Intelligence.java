package ee.taltech.iti0200.ai;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.di.factory.BotFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class Intelligence implements Component {

    private final List<Bot> bots = new ArrayList<>();
    private final World world;
    private final BotFactory botFactory;
    private final EventBus eventBus;

    @Inject
    public Intelligence(World world, BotFactory botFactory, EventBus eventBus) {
        this.world = world;
        this.botFactory = botFactory;
        this.eventBus = eventBus;
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

        Iterator<Bot> iterator = bots.iterator();
        while (iterator.hasNext()) {
            Bot bot = iterator.next();
            if (bot.isAlive()) {
                bot.update(tick);
            } else {
                iterator.remove();
            }
        }

        if (tick % 300 == 0) {
            if (2 * bots.size() < world.getLivingEntities().size() + 1) {
                Bot bot = botFactory.create();
                bots.add(bot);
                eventBus.dispatch(new CreateEntity(bot, EVERYONE));
            }
        }
    }

}
