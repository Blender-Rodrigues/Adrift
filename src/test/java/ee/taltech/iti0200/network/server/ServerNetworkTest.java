package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.application.RestartGame;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.UpdateScore;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static ee.taltech.iti0200.network.message.Receiver.ALL_CLIENTS;
import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class ServerNetworkTest {

    private ServerNetwork network;
    private Set<ConnectionToClient> clients;
    private World world;
    private EventBus eventBus;
    private Messenger messenger;
    private ArgumentCaptor<List<Message>> messageCaptor;

    @BeforeEach
    void setUp() {
        clients = new HashSet<>();
        eventBus = mock(EventBus.class, RETURNS_DEEP_STUBS);
        world = mock(World.class, RETURNS_DEEP_STUBS);
        messenger = mock(Messenger.class);
        Registrar registrar = mock(Registrar.class);
        ServerSocket serverSocket = mock(ServerSocket.class);
        messageCaptor = ArgumentCaptor.forClass(List.class);
        network = new ServerNetwork(world, serverSocket, eventBus, clients, messenger, registrar);
    }

    @Test
    void propagatePassesFilteredMessagesToOutbox() {
        Event serverOnly = new UpdateScore(new HashMap<>(), SERVER);
        Event everyone = new UpdateScore(new HashMap<>(), EVERYONE);
        Event clientsOnly = new UpdateScore(new HashMap<>(), ALL_CLIENTS);
        Event specificClient = new UpdateScore(new HashMap<>(), new Receiver(UUID.randomUUID()));

        when(eventBus.propagateAll()).thenReturn(asList(serverOnly, everyone, clientsOnly, specificClient));

        network.propagate(5L);

        verify(messenger).writeOutbox(messageCaptor.capture());

        List<Message> actual = messageCaptor.getValue();

        assertThat(actual).hasSize(3);
        assertThat(actual).doesNotContain(serverOnly);
        assertThat(actual).contains(everyone, clientsOnly, specificClient);
        assertThat(everyone.getReceiver()).isEqualTo(ALL_CLIENTS);
    }

    @Test
    void propagateSendsVelocityUpdatesToOutboxIfBodyHasMoved() {
        UUID id1 = UUID.fromString("216db3c6-4880-485c-aac9-6229e1295400");
        Vector position1 = new Vector(1, 3);
        Vector speed1 = new Vector(5, 7);
        Entity entity1 = mock(Entity.class, RETURNS_DEEP_STUBS);
        when(entity1.getId()).thenReturn(id1);
        when(entity1.getBoundingBox().getCentre()).thenReturn(position1);
        when(entity1.getSpeed()).thenReturn(speed1);
        when(entity1.hasMoved()).thenReturn(true);

        Entity entity2 = mock(Entity.class);
        when(entity2.hasMoved()).thenReturn(false);

        when(world.getMovableBodies()).thenReturn(asList(entity1, entity2));
        when(eventBus.propagateAll()).thenReturn(new ArrayList<>());

        network.propagate(5L);

        verify(messenger).writeOutbox(messageCaptor.capture());

        List<Message> actual = messageCaptor.getValue();
        UpdateVector message = (UpdateVector) actual.get(0);

        assertThat(actual).hasSize(1);

        assertThat(message.getId()).isEqualTo(id1);
        assertThat(message.getPosition()).isEqualTo(position1);
        assertThat(message.getSpeed()).isEqualTo(speed1);
        assertThat(message.getTick()).isEqualTo(5L);
        assertThat(message.getReceiver()).isEqualTo(ALL_CLIENTS);
    }

    @Test
    void propagateThrowsRecreateIfPlayersHaveLeft() {
        when(world.getEntitiesRemoved()).thenReturn(1L);

        Throwable throwable = catchThrowable(() -> network.propagate(1L));

        assertThat(throwable)
            .isInstanceOf(RestartGame.class)
            .hasNoCause();

        verifyNoInteractions(eventBus);
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
