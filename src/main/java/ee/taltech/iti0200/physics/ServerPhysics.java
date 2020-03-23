package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Projectile;

import java.util.List;

public class ServerPhysics extends Physics {

    public ServerPhysics(World world) {
        super(world);
    }

    @Override
    public void update(long tick) {
        super.update(tick);
        checkForProjectileHits(
            world.getLivingEntities(),
            world.getProjectiles()
        );
    }

    private void checkForProjectileHits(List<Living> living, List<Projectile> projectiles) {
        for (Projectile projectile: projectiles) {
            living.stream()
                .filter(projectile::intersects)
                .findAny()
                .ifPresent(projectile::onCollide);
        }
    }

}
