package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.common.MoveBodyHandler;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
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
import static org.mockito.Mockito.verifyZeroInteractions;
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

        handler.handle(new UpdateVector(entity, EVERYONE));

        verifyZeroInteractions(world);
    }

    @Test
    void handleIgnoresUpdateIfEntityNotInWorld() {
        when(entity.getId()).thenReturn(UUID.randomUUID());

        handler.handle(new UpdateVector(entity, EVERYONE));

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

        handler.handle(new UpdateVector(entity, EVERYONE));

        verify(localEntity).setPosition(new Vector(5, 7));
        verify(localEntity).setSpeed(new Vector(1, 3));
    }

}
