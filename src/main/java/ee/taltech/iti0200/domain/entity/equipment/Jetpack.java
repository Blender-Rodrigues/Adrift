package ee.taltech.iti0200.domain.entity.equipment;

import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;

public class Jetpack extends Equipment {

    private static final int JACKPOT_CHARGES = 10;
    private int charges;

    public Jetpack(BoundingBox boundingBox) {
        super(boundingBox);
        charges = JACKPOT_CHARGES;
    }

    public void use() {
        if (charges >= 1) {
            owner.accelerate(new Vector(0.0, ((Player)owner).getJumpDeltaV() / 1.5));
            charges --;
        }

        if (charges <= 0) {
            owner.removeEquipment(this);
        }
    }

    public int getCharges() {
        return charges;
    }

    public Jetpack setOwner(Living owner) {
        this.owner = owner;
        this.boundingBox = owner.getBoundingBox();
        return this;
    }

}
