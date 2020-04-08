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
    private Map<Living, Consumable> loots;

    @Inject
    public ConsumableFactory(World world) {
        this.world = world;
        loots = new HashMap<>();
    }

    public Consumable create(Living victim) {
        if (!loots.containsKey(victim)) {
            Consumable loot = new HealthGlobe(victim.getBoundingBox().getCentre(), world);
            loots.put(victim, loot);
            return loot;
        }
        return null;
    }

}
