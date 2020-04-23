package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.ChangeEquipment;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.message.LoadWorld;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.physics.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
class ClientNetworkTest {

    private World world;
    private EventBus eventBus;
    private Player player;
    private ConnectionToServer connection;
    private Messenger messenger;
    private ClientNetwork network;
    private ArgumentCaptor<List<Message>> messageCaptor;

    @BeforeEach
    void setUp() {
        world = mock(World.class);
        eventBus = mock(EventBus.class);
        player = mock(Player.class, RETURNS_DEEP_STUBS);
        connection = mock(ConnectionToServer.class);
        messenger = mock(Messenger.class);
        messageCaptor = ArgumentCaptor.forClass(List.class);
        network = new ClientNetwork(world, eventBus, player, connection, messenger);
    }

    @Test
    void initializeLoadsServerResponseToWorld() throws IOException, ClassNotFoundException {
        Entity fromServer = mock(Entity.class);
        Entity anotherOne = mock(Entity.class);
        Vector spawn = new Vector(1, 3);
        LoadWorld worldData = mock(LoadWorld.class);

        when(connection.getWorldData()).thenReturn(worldData);
        when(worldData.getEntities()).thenReturn(asList(fromServer, anotherOne));
        when(worldData.getSpawn()).thenReturn(spawn);

        network.initialize();

        verify(connection).initialize();
        verify(worldData, times(2)).getEntities();
        verify(world).addEntity(fromServer);
        verify(world).addEntity(anotherOne);
        verify(world).mapTerrain();
        verify(player).setPosition(spawn);
    }

    @Test
    void propagatePassesFilteredMessagesToOutbox() {
        Event clientOnly = new ChangeEquipment(player, 1, new Receiver(emptyList()));
        Event everyone = new ChangeEquipment(player, 1, EVERYONE);
        Event server = new ChangeEquipment(player, 1, SERVER);

        when(eventBus.propagateAll()).thenReturn(asList(clientOnly, everyone, server));

        network.propagate(5L);

        verify(messenger).writeOutbox(messageCaptor.capture());

        List<Message> actual = messageCaptor.getValue();

        assertThat(actual).hasSize(3);
        assertThat(actual).doesNotContain(clientOnly);
        assertThat(actual).contains(everyone, server);
        assertThat(everyone.getReceiver()).isEqualTo(SERVER);
    }

    @Test
    void propagateAddsPlayerLocationMessage() {
        UUID id = UUID.fromString("216db3c6-4880-485c-aac9-6229e1295400");
        Vector position = new Vector(1, 3);
        Vector speed = new Vector(5, 7);

        when(eventBus.propagateAll()).thenReturn(new ArrayList<>());
        when(player.getId()).thenReturn(id);
        when(player.getBoundingBox().getCentre()).thenReturn(position);
        when(player.getSpeed()).thenReturn(speed);

        network.propagate(5L);

        verify(messenger).writeOutbox(messageCaptor.capture());

        List<Message> actual = messageCaptor.getValue();
        UpdateVector message = (UpdateVector) actual.get(0);

        assertThat(actual).hasSize(1);
        assertThat(message.getId()).isEqualTo(id);
        assertThat(message.getPosition()).isEqualTo(position);
        assertThat(message.getSpeed()).isEqualTo(speed);
        assertThat(message.getTick()).isEqualTo(5L);
        assertThat(message.getReceiver()).isEqualTo(SERVER);
    }

    @Test
    void terminateDelegatesToComponents() {
        network.terminate();

        verify(messenger).terminate();
        verify(connection).close();
    }

}
