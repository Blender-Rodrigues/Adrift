package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.physics.Vector;

public class Intelligence implements Component {

    private World world;

    public Intelligence(World world) {
        this.world = world;
    }

    @Override
    public void initialize() {
        world.addEntity(new Bot(new Vector(10.0, 4.0), world));
        world.addEntity(new Bot(new Vector(30.0, 4.0), world));
    }

    @Override
    public void update(long tick) {

    }

}
