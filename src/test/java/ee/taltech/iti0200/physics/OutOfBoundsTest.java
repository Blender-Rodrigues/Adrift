package ee.taltech.iti0200.physics;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OutOfBoundsTest {

    @Test
    void testMoveEntityOutOfBounds() {
        ArgumentCaptor<RemoveEntity> captor = ArgumentCaptor.forClass(RemoveEntity.class);
        UUID id = UUID.randomUUID();

        World world = new World(0, 0, 100, 100, 0.05);
        EventBus eventBus = mock(EventBus.class);
        Physics physics = new ServerPhysics(world, eventBus);
        Player player = new Player(new Vector(50, 50), world);
        player.setId(id);
        world.mapTerrain();
        world.addEntity(player);

        player.move(new Vector(100, 0));
        physics.update(1);

        verify(eventBus).dispatch(captor.capture());
        RemoveEntity event = captor.getValue();
        assertThat(event.getReceiver()).isEqualTo(EVERYONE);
        assertThat(event.getId()).isEqualTo(id);
    }
}
