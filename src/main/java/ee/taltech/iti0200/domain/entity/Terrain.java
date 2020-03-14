package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.physics.Body;
import ee.taltech.iti0200.physics.Vector;

public class Terrain extends Entity {

    private static final Vector size = new Vector(2.00, 2.00);
    public static final double TERRAIN_BLOCK_RESOLUTION = 100;
    private static final double mass = Double.POSITIVE_INFINITY;
    private static final double elasticity = 0.9;
    private static final double frictionCoefficient = 0.9;
    private static final int MAX_HEALTH = 100;

    private int health;
    private World world;

    public Terrain(Vector position, World world) {
        super(new Body(mass, new Vector(size), position.rounded(), true, true), true);
        setElasticity(elasticity);
        setFrictionCoefficient(frictionCoefficient);
        health = MAX_HEALTH;
        this.world = world;
    }

    public int getIntegerWidth() {
        return (int) getBoundingBox().getSize().getX() * (int) TERRAIN_BLOCK_RESOLUTION;
    }

    public void onCollide(Body otherBody) {
        if (otherBody instanceof Projectile) {
            this.health -= ((Projectile) otherBody).getDamage();
            if (health <= 0) {
                world.removeTerrain(this);
            }
        }
    }

}
