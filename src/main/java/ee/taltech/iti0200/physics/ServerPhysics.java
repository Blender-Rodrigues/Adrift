package ee.taltech.iti0200.physics;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Projectile;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
                .ifPresent(entity -> collisions.add(new ImmutablePair<>(projectile, entity)));
        }
    }

    @Override
    protected List<Entity> moveBodies(List<Entity> bodiesToMove, double timeStep) {
        return super.moveBodies(bodiesToMove, timeStep).stream()
            .filter(entity -> {
                if (world.entityOutOfBounds(entity)) {
                    eventBus.dispatch(new RemoveEntity(entity, Receiver.EVERYONE));
                    return false;
                } else {
                    return true;
                }
            }).collect(Collectors.toList());
    }

}
