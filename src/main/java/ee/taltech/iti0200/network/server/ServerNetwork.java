package ee.taltech.iti0200.network.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.RecreateException;
import ee.taltech.iti0200.di.annotations.ServerClients;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class ServerNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ServerNetwork.class);
    private final Set<ConnectionToClient> clients;
    private final Messenger messenger;
    private final Registrar registrar;
    private final ServerSocket serverSocket;

    @Inject
    public ServerNetwork(
        World world,
        ServerSocket serverSocket,
        EventBus eventBus,
        @ServerClients Set<ConnectionToClient> clients,
        Messenger messenger,
        Registrar registrar
    ) {
        super(world, eventBus);
        this.serverSocket = serverSocket;
        this.clients = clients;
        this.messenger = messenger;
        this.registrar = registrar;
    }

    @Override
    public void initialize() {
        registrar.start();
    }

    @Override
    protected Messenger getMessenger() {
        return messenger;
    }

    @Override
    public void propagate(long tick) {
        Thread.yield();

        checkDisconnect();

        List<Message> events = eventBus.propagateAll()
            .stream()
            .filter(event -> {
                Receiver receiver = event.getReceiver();
                if (receiver.equals(EVERYONE)) {
                    event.setReceiver(Receiver.ALL_CLIENTS);
                    return true;
                }
                return !event.getReceiver().equals(Receiver.SERVER);
            })
            .collect(Collectors.toList());

        // TODO: filter out only those who have moved
        world.getMovableBodies().forEach(body -> events.add(new UpdateVector(body, Receiver.ALL_CLIENTS)));

        if (events.isEmpty()) {
            return;
        }

        messenger.writeOutbox(events);
    }

    @Override
    public void terminate() {
        super.terminate();
        clients.forEach(ConnectionToClient::close);
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Failed to close " + serverSocket.getClass() + ": " + e.getMessage(), e);
        }
    }

    private void checkDisconnect() {
        synchronized(clients) {
            Iterator<ConnectionToClient> iterator = clients.iterator();

            while (iterator.hasNext()) {
                ConnectionToClient connection = iterator.next();

                if (!connection.isFinalized()) {
                    continue;
                }
                if (connection.isOpen()) {
                    continue;
                }

                eventBus.dispatch(new RemoveEntity(connection.getId(), EVERYONE));
                iterator.remove();
            }
            if (clients.size() == 0 && world.getEntitiesRemoved() > 0) {
                logger.warn("Last player left, recreating the game");
                throw new RecreateException();
            }
        }
    }

}
