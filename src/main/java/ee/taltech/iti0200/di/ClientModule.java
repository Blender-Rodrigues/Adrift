package ee.taltech.iti0200.di;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import ee.taltech.iti0200.application.ClientGame;
import ee.taltech.iti0200.application.Game;
import ee.taltech.iti0200.di.annotations.ConcurrentInbox;
import ee.taltech.iti0200.di.annotations.ConcurrentTcpOutbox;
import ee.taltech.iti0200.di.annotations.ConcurrentUdpOutbox;
import ee.taltech.iti0200.di.annotations.ConnectionAlive;
import ee.taltech.iti0200.di.annotations.GameId;
import ee.taltech.iti0200.di.annotations.ServerHost;
import ee.taltech.iti0200.di.annotations.ServerTcpPort;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.client.ClientCollisionHandler;
import ee.taltech.iti0200.domain.event.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.common.CollisionHandler;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.client.ClientMessenger;
import ee.taltech.iti0200.network.client.ClientNetwork;
import ee.taltech.iti0200.network.client.ConnectionToServer;
import ee.taltech.iti0200.network.message.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.net.InetAddress.getByName;

public class ClientModule extends CommonModule {

    private final String host;
    private final int port;

    public ClientModule(String host, int port, String playerName) {
        this.host = host;
        this.port = port;
        if (playerName == null) {
            playerName = id.toString().replaceAll("[^\\d]", "").substring(0, 5);
        }
        player.setName(playerName);
    }

    protected void configure() {
        super.configure();
        bind(Key.get(UUID.class, GameId.class)).toInstance(id);
        bind(Key.get(Integer.class, ServerTcpPort.class)).toInstance(port);
        bind(Key.get(new TypeLiteral<ConcurrentLinkedQueue<Message>>() {}, ConcurrentInbox.class)).toInstance(new ConcurrentLinkedQueue<>());
        bind(Key.get(new TypeLiteral<ConcurrentLinkedQueue<Message>>() {}, ConcurrentTcpOutbox.class)).toInstance(new ConcurrentLinkedQueue<>());
        bind(Key.get(new TypeLiteral<ConcurrentLinkedQueue<Message>>() {}, ConcurrentUdpOutbox.class)).toInstance(new ConcurrentLinkedQueue<>());
        bind(Key.get(AtomicBoolean.class, ConnectionAlive.class)).toInstance(new AtomicBoolean(true));

        try {
            bind(Key.get(InetAddress.class, ServerHost.class)).toInstance(getByName(host));
        } catch (UnknownHostException e) {
            addError(e);
        }

        bind(Game.class).to(ClientGame.class);
        bind(CollisionHandler.class).to(ClientCollisionHandler.class);

        bind(EventBus.class).in(Singleton.class);
        bind(ConnectionToServer.class).in(Singleton.class);
        bind(EntityDamageHandler.class).in(Singleton.class);
        bind(Network.class).to(ClientNetwork.class).in(Singleton.class);
        bind(Messenger.class).to(ClientMessenger.class).in(Singleton.class);
    }

}
