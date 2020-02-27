package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.application.Component;
import ee.taltech.iti0200.domain.World;

public class Intelligence implements Component {

    private World world;

    public Intelligence(World world) {
        this.world = world;
    }

    @Override
    public void update(long tick) {

    }

}
