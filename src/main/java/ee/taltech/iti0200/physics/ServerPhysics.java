package ee.taltech.iti0200.physics;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.GameId;
import ee.taltech.iti0200.domain.Fall;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static java.lang.Integer.MAX_VALUE;

public class ServerPhysics extends Physics {

    @Inject
    public ServerPhysics(World world, EventBus eventBus, @GameId UUID id) {
        super(world, eventBus, id);
        collisionReceiver = EVERYONE;
    }

    @Override
    public void update(long tick) {
        super.update(tick);
        collisions = new HashSet<>();
        checkForConsumableHits(world.getLivingEntities(), world.getConsumables());
        dispatchCollisions();
    }

    private void checkForConsumableHits(List<Living> living, List<Consumable> consumables) {
        for (Consumable consumable: consumables) {
            living.stream()
                .filter(consumable::intersects)
                .findAny()
                .ifPresent(entity -> collisions.add(new ImmutablePair<>(consumable, entity)));
        }
    }

    @Override
    protected List<Entity> moveBodies(List<Entity> bodiesToMove, double timeStep) {
        return super.moveBodies(bodiesToMove, timeStep).stream()
            .filter(entity -> {
                if (!world.entityOutOfBounds(entity)) {
                    return true;
                }
                if (entity instanceof Damageable) {
                    eventBus.dispatch(new DealDamage(new Fall(MAX_VALUE), (Damageable) entity, EVERYONE));
                } else {
                    eventBus.dispatch(new RemoveEntity(entity, EVERYONE));
                }
                return false;
            }).collect(Collectors.toList());
    }

}
