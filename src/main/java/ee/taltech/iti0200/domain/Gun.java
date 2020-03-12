package ee.taltech.iti0200.domain;

import ee.taltech.iti0200.physics.Vector;

public class Gun {

    private static final double fireRate = 3;
    private static final double damage = 1;
    private static final double projectileSpeed = 15;

    private double leftToReload;

    public Gun() {
        leftToReload = 0;
    }

    public boolean canShoot() {
        return leftToReload <= 0;
    }

    public double getProjectileSpeed() {
        return projectileSpeed;
    }

    public void passTime(double time) {
        leftToReload -= time;
    }

    public Projectile shoot(Vector direction, Vector position) {
        leftToReload = fireRate;

        Vector speed = new Vector(direction);
        speed.normalize();
        speed.scale(projectileSpeed);

        return new Projectile(new Vector(position), speed);
    }
}
