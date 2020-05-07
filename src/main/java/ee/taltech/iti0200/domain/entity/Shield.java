package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

import java.util.Random;

public class Shield extends Consumable implements HealingSource {

    private static final Vector SIZE = new Vector(1, 1);
    private int shieldAmount = 20 + new Random().nextInt(40);

    public Shield(Vector position) {
        super(new BoundingBox(position, SIZE));
    }

    @Override
    public int getHealing() {
        return shieldAmount;
    }
}
