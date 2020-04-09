package ee.taltech.iti0200.di.factory;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.physics.Vector;

import java.util.HashMap;
import java.util.Map;

public class ConsumableFactory {

    private World world;

    @Inject
    public ConsumableFactory(World world) {
        this.world = world;
    }

    public Consumable create(Vector position) {
        Consumable loot = new HealthGlobe(position, world);
        return loot;
    }

}
