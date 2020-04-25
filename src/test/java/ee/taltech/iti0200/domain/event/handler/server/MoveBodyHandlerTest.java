package ee.taltech.iti0200.domain.event.handler.server;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.domain.event.handler.common.MoveBodyHandler;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MoveBodyHandlerTest {

    private Entity entity;
    private World world;
    private UUID localId;
    private MoveBodyHandler handler;

    @BeforeEach
    void setUp() {
        entity = mock(Entity.class, RETURNS_DEEP_STUBS);
        world = mock(World.class);
        localId = UUID.randomUUID();
        handler = new MoveBodyHandler(world, localId);
    }

    @Test
    void handleIgnoresLocalEvents() {
        when(entity.getId()).thenReturn(localId);

        handler.handle(new UpdateVector(entity, 1L, EVERYONE));

        verifyNoInteractions(world);
    }

    @Test
    void handleIgnoresUpdateIfEntityNotInWorld() {
        when(entity.getId()).thenReturn(UUID.randomUUID());

        handler.handle(new UpdateVector(entity, 1L, EVERYONE));

        verify(entity, never()).setPosition(any());
        verify(entity, never()).setSpeed(any());
    }

    @Test
    void handleSetsSpeedAndPositionOnEntity() {
        UUID entityId = UUID.randomUUID();
        Entity localEntity = mock(Entity.class);

        when(entity.getSpeed()).thenReturn(new Vector(1, 3));
        when(entity.getBoundingBox().getCentre()).thenReturn(new Vector(5, 7));
        when(entity.getId()).thenReturn(entityId);
        when(world.getEntity(entityId)).thenReturn(localEntity);

        handler.handle(new UpdateVector(entity, 1L, EVERYONE));

        verify(localEntity).setPosition(new Vector(5, 7));
        verify(localEntity).setSpeed(new Vector(1, 3));
    }

    @Test
    void handleIgnoresOutOfOrderUpdatesForLiving() {
        UUID entityId = UUID.randomUUID();
        Living localEntity = mock(Living.class);

        when(entity.getSpeed()).thenReturn(new Vector(1, 3));
        when(entity.getBoundingBox().getCentre()).thenReturn(new Vector(5, 7));
        when(entity.getId()).thenReturn(entityId);
        when(world.getEntity(entityId)).thenReturn(localEntity);

        handler.handle(new UpdateVector(entity, 5L, EVERYONE));

        when(entity.getSpeed()).thenReturn(new Vector(7, 11));
        when(entity.getBoundingBox().getCentre()).thenReturn(new Vector(13, 17));

        handler.handle(new UpdateVector(entity, 4L, EVERYONE));

        verify(localEntity).setPosition(new Vector(5, 7));
        verify(localEntity).setSpeed(new Vector(1, 3));
        verify(localEntity, never()).setPosition(new Vector(13, 17));
        verify(localEntity, never()).setSpeed(new Vector(7, 11));
    }

}
