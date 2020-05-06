package ee.taltech.iti0200.di.factory;

import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.physics.Vector;

public class LootFactory {

    public Consumable create(Vector position) {
        return new HealthGlobe(position);
    }

}
