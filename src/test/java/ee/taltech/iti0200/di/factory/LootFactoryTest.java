package ee.taltech.iti0200.di.factory;

import ee.taltech.iti0200.domain.entity.HealthGlobe;
import ee.taltech.iti0200.domain.entity.equipment.FastGun;
import ee.taltech.iti0200.domain.entity.equipment.SpecialGun;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LootFactoryTest {

    @Test
    void testLootFactoryCreatesVariousLoot() {
        Vector position = new Vector(5, 5);
        Random random = mock(Random.class);
        LootFactory lootFactory = new LootFactory(random);

        when(random.nextDouble()).thenReturn(LootFactory.SPECIAL_GUN_CHANCE);
        assertTrue(lootFactory.create(position) instanceof SpecialGun);

        when(random.nextDouble()).thenReturn(LootFactory.FAST_GUN_CHANCE);
        assertTrue(lootFactory.create(position) instanceof FastGun);

        when(random.nextDouble()).thenReturn(1d);
        assertTrue(lootFactory.create(position) instanceof HealthGlobe);
    }

}