package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.Body;

import java.util.List;

public class Living extends Entity {

    protected final World world;
    protected int health;

    public Living(List<Body> components, boolean collideable, World world) {
        super(components, collideable);
        this.world = world;
    }

    public Living(Body component, boolean collideable, World world) {
        super(component, collideable);
        this.world = world;
    }

    /**
     * Called for living things on every game tick
     */
    public void update(long tick) {
        if (health <= 0) {
            this.removed = true;
        }
    }

    public void onCollide(Body otherBody) {
        if (otherBody instanceof Projectile && ((Projectile) otherBody).getOwner() != this) {
            health -= ((Projectile) otherBody).getDamage();
        }
    }

}
