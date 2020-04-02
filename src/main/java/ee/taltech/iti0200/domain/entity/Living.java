package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.BoundingBox;

public class Living extends Damageable {

    private static final long serialVersionUID = 1L;

    protected transient World world;
    protected boolean alive = true;
    protected Gun gun;

    public Living(double mass, BoundingBox boundingBox, World world, int health) {
        super(mass, boundingBox, health);
        this.world = world;
        this.movable = true;
    }

    public Gun getGun() {
        return gun;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
        gun.setOwner(this);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isAlive() {
        return alive;
    }

    public Living setAlive(boolean alive) {
        this.alive = alive;
        return this;
    }

}
