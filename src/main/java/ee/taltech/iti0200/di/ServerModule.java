package ee.taltech.iti0200.di;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import ee.taltech.iti0200.application.Game;
import ee.taltech.iti0200.application.ServerGame;
import ee.taltech.iti0200.di.annotations.ConcurrentInbox;
import ee.taltech.iti0200.di.annotations.ConnectionAlive;
import ee.taltech.iti0200.di.annotations.GameId;
import ee.taltech.iti0200.di.annotations.ServerClients;
import ee.taltech.iti0200.di.annotations.ServerTcpPort;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.handler.common.CollisionHandler;
import ee.taltech.iti0200.domain.event.handler.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.handler.server.ServerCollisionHandler;
import ee.taltech.iti0200.domain.event.handler.server.ServerDamageHandler;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.server.ConnectionToClient;
import ee.taltech.iti0200.network.server.GroupMessenger;
import ee.taltech.iti0200.network.server.ServerNetwork;
import ee.taltech.iti0200.physics.Physics;
import ee.taltech.iti0200.physics.ServerPhysics;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

public class ServerModule extends CommonModule {

    public static final UUID SERVER_ID = new UUID(0, 0);

    private int port;

    public ServerModule(int port) {
        this.port = port;
    }

    protected void configure() {
        super.configure();
        bind(Key.get(UUID.class, GameId.class)).toInstance(SERVER_ID);
        bind(Key.get(Integer.class, ServerTcpPort.class)).toInstance(port);
        bind(Key.get(new TypeLiteral<Set<ConnectionToClient>>() {}, ServerClients.class)).toInstance(newKeySet());
        bind(Key.get(AtomicBoolean.class, ConnectionAlive.class)).toInstance(new AtomicBoolean(true));
        bind(Key.get(new TypeLiteral<ConcurrentLinkedQueue<Message>>() {}, ConcurrentInbox.class)).toInstance(new ConcurrentLinkedQueue<>());

        try {
            bind(ServerSocket.class).toInstance(new ServerSocket(port));
        } catch (IOException e) {
            addError(e);
        }

        bind(Game.class).to(ServerGame.class);

        bind(EventBus.class).in(Singleton.class);
        bind(Physics.class).to(ServerPhysics.class).in(Singleton.class);
        bind(CollisionHandler.class).to(ServerCollisionHandler.class).in(Singleton.class);
        bind(EntityDamageHandler.class).to(ServerDamageHandler.class).in(Singleton.class);
        bind(Network.class).to(ServerNetwork.class).in(Singleton.class);
        bind(Messenger.class).to(GroupMessenger.class).in(Singleton.class);
    }

}
