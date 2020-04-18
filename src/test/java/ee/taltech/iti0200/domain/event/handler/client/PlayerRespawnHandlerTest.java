package ee.taltech.iti0200.domain.event.handler.client;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.entity.RespawnPlayer;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerRespawnHandlerTest {

    @Test
    void handle() {
        UUID id = UUID.randomUUID();
        Vector position = new Vector(1, 3);

        Player player = mock(Player.class, RETURNS_DEEP_STUBS);
        World world = mock(World.class);

        when(world.getEntity(id)).thenReturn(player);
        when(player.getId()).thenReturn(id);
        when(player.getMaxHealth()).thenReturn(19);

        RespawnPlayer event = new RespawnPlayer(id, position, 5, null);

        new PlayerRespawnHandler(world).handle(event);

        verify(player).setHealth(19);
        verify(player).setLives(5);
        verify(player).setPosition(position);
    }

}
