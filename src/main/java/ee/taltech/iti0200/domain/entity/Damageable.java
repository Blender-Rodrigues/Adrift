package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.graphics.Shader;
import ee.taltech.iti0200.graphics.ViewPort;
import ee.taltech.iti0200.physics.BoundingBox;

public class Damageable extends Entity {

    private static final long serialVersionUID = 1L;

    protected int health;
    protected int maxHealth;
    protected int shield;

    public Damageable(double mass, BoundingBox boundingBox, int health) {
        super(mass, boundingBox);
        this.health = health;
        this.maxHealth = health;
        this.shield = 0;
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void renderShield(Shader shader, ViewPort viewPort, long tick) {
        if (shield > 0) {
            renderers.get("shield").render(shader, viewPort, tick);
        }
    }

}
