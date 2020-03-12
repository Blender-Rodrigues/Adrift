package ee.taltech.iti0200.domain;

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
}
