package ee.taltech.iti0200.domain.event.handler.common;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.Terrain;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.physics.BoundingBox;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static ee.taltech.iti0200.domain.entity.Player.JUMP_AMOUNT_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CollisionHandlerTest {

    private World world;
    private CollisionHandler handler;

    @BeforeEach
    void setUp() {
        world = mock(World.class);

        handler = new CollisionHandler(world);
    }

    @Test
    void handleStopsMessageIfEntityNotInWorld() {
        UUID id = UUID.randomUUID();

        Entity entity = mock(Entity.class);
        when(entity.getId()).thenReturn(id);
        when(world.getEntity(id)).thenReturn(null);

        EntityCollide event = new EntityCollide(entity, null, null);

        handler.handle(event);

        assertThat(event.isStopped()).isTrue();
        verify(world).getEntity(id);
    }

    @Test
    void handleStopsMessageIfOtherEntityNotInWorld() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);

        when(entity1.getId()).thenReturn(id1);
        when(entity2.getId()).thenReturn(id2);

        when(world.getEntity(id1)).thenReturn(entity1);
        when(world.getEntity(id2)).thenReturn(null);

        EntityCollide event = new EntityCollide(entity1, entity2, null);

        handler.handle(event);

        assertThat(event.isStopped()).isTrue();
        verify(world).getEntity(id1);
        verify(world).getEntity(id2);
    }

    @Test
    void handleIncreasesPlayerJumpIfHitGroundBelow() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        BoundingBox playerBox = new BoundingBox(new Vector(5, 5), new Vector(1, 1));
        BoundingBox terrainBox = new BoundingBox(new Vector(5, 4), new Vector(1, 1));

        Player player = mock(Player.class);
        Terrain terrain = mock(Terrain.class);

        when(player.getId()).thenReturn(id1);
        when(terrain.getId()).thenReturn(id2);

        when(player.getBoundingBox()).thenReturn(playerBox);
        when(terrain.getBoundingBox()).thenReturn(terrainBox);

        when(world.getEntity(id1)).thenReturn(player);
        when(world.getEntity(id2)).thenReturn(terrain);

        EntityCollide event = new EntityCollide(player, terrain, null);

        handler.handle(event);

        assertThat(event.isStopped()).isFalse();
        verify(player).setJumpsLeft(JUMP_AMOUNT_LIMIT);
    }

}
