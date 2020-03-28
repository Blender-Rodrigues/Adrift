package ee.taltech.iti0200.physics;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.EventBus;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.List;

public class ServerPhysics extends Physics {

    @Inject
    public ServerPhysics(World world, EventBus eventBus) {
        super(world, eventBus);
    }

    @Override
    public void update(long tick) {
        super.update(tick);
        collisions = new HashSet<>();
        checkForProjectileHits(world.getLivingEntities(), world.getProjectiles());
        dispatchCollisions();
    }

    private void checkForProjectileHits(List<Living> living, List<Projectile> projectiles) {
        for (Projectile projectile: projectiles) {
            living.stream()
                .filter(projectile::intersects)
                .findAny()
                .ifPresent(entity -> collisions.add(new Pair<>(projectile, entity)));
        }
    }

}
