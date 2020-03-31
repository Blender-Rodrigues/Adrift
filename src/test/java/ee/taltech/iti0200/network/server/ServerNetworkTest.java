package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.application.RecreateException;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.network.Messenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

class ServerNetworkTest {

    private ServerNetwork network;
    private Set<ConnectionToClient> clients;
    private World world;
    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        clients = new HashSet<>();
        eventBus = mock(EventBus.class, RETURNS_DEEP_STUBS);
        world = mock(World.class, RETURNS_DEEP_STUBS);
        Messenger messenger = mock(Messenger.class);
        Registrar registrar = mock(Registrar.class);
        ServerSocket serverSocket = mock(ServerSocket.class);
        network = new ServerNetwork(world, serverSocket, eventBus, clients, messenger, registrar);
    }

    @Test
    void propagateThrowsRecreateIfPlayersHaveLeft() {
        when(world.getEntitiesRemoved()).thenReturn(1L);

        Throwable throwable = catchThrowable(() -> network.propagate(1L));

        assertThat(throwable)
            .isInstanceOf(RecreateException.class)
            .hasNoCause();

        verifyZeroInteractions(eventBus);
    }

    @Test
    void propagateRemovesDisconnectedClients() {
        // given
        ArgumentCaptor<RemoveEntity> captor = ArgumentCaptor.forClass(RemoveEntity.class);
        UUID id = UUID.randomUUID();

        ConnectionToClient open = mock(ConnectionToClient.class);
        ConnectionToClient closed = mock(ConnectionToClient.class);
        ConnectionToClient notFinalized = mock(ConnectionToClient.class);

        clients.addAll(asList(open, closed, notFinalized));

        when(eventBus.propagateAll()).thenReturn(emptyList());
        when(open.isOpen()).thenReturn(true);
        when(open.isFinalized()).thenReturn(true);
        when(closed.isOpen()).thenReturn(false);
        when(closed.isFinalized()).thenReturn(true);
        when(notFinalized.isOpen()).thenReturn(true);
        when(notFinalized.isFinalized()).thenReturn(false);
        when(closed.getId()).thenReturn(id);

        // when
        network.propagate(1L);

        // then
        assertThat(clients).containsExactlyInAnyOrder(open, notFinalized);

        verify(eventBus).dispatch(captor.capture());

        RemoveEntity event = captor.getValue();
        assertThat(event.getReceiver()).isEqualTo(EVERYONE);
        assertThat(event.getId()).isEqualTo(id);
    }

}
