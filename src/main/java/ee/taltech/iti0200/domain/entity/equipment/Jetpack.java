package ee.taltech.iti0200.domain.entity.equipment;

import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.joml.Vector3f;

public class Jetpack extends Equipment {

    private static final int JACKPOT_CHARGES = 300;
    public static final int MAXIMUM_SPEED = 7;
    public static final double ACCELERATION = 1.2;
    private int charges;
    private long lastUsed;

    public Jetpack(BoundingBox boundingBox) {
        super(boundingBox);
        charges = JACKPOT_CHARGES;
        lastUsed = 0;
    }

    public void use(long currentTick) {
        if (lastUsed != currentTick && charges >= 1) {

            owner.accelerate(new Vector(0.0, Math.max(0, Math.min(MAXIMUM_SPEED - owner.getSpeed().y, ACCELERATION))));
            charges--;
            lastUsed = currentTick;
        }

        if (charges <= 0) {
            owner.removeEquipment(this);
        }
    }

    public Jetpack setOwner(Living owner) {
        this.owner = owner;
        this.boundingBox = owner.getBoundingBox();
        return this;
    }

    @Override
    public void render(Shader shader, ViewPort viewPort, long tick) {
        if (lastUsed + 5 >= tick) {
            renderers.get("FIRE").render(shader, viewPort, tick);
        }
    }

}
