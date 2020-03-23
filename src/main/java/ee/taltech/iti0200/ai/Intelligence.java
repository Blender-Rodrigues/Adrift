package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.physics.Vector;

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
        bots.add(new Bot(new Vector(10.0, 4.0), world));
        bots.add(new Bot(new Vector(30.0, 4.0), world));

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

}
