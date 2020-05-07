package ee.taltech.iti0200.di.factory;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.Loot;
import ee.taltech.iti0200.domain.entity.equipment.FastGun;
import ee.taltech.iti0200.domain.entity.equipment.SpecialGun;
import ee.taltech.iti0200.physics.Vector;

import java.util.Random;

public class LootFactory {

    private Random random;

    public static final double SPECIAL_GUN_CHANCE = 0.1;
    public static final double FAST_GUN_CHANCE = 0.1 + SPECIAL_GUN_CHANCE;

    @Inject
    public LootFactory(Random random) {
        this.random = random;
    }

    public Loot create(Vector position) {
        double randomDouble = random.nextDouble();
        if (randomDouble <= SPECIAL_GUN_CHANCE) {
            return new SpecialGun(position);
        } else if (randomDouble <= FAST_GUN_CHANCE) {
            return new FastGun(position);
        } else {
            return new HealthGlobe(position);
        }
    }

}