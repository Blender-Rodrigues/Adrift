package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.entity.CreatePlayer;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Ping;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ee.taltech.iti0200.application.Game.eventBus;
import static ee.taltech.iti0200.application.ServerGame.SERVER_ID;
import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class ServerNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ServerNetwork.class);
    private final Set<ConnectionToClient> clients = ConcurrentHashMap.newKeySet();
    private final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final Messenger messenger = new GroupMessenger(clients, inbox, alive);

    private ServerSocket serverSocket;

    public ServerNetwork(World world, int tcpPort) throws IOException {
        super(world);
        serverSocket = new ServerSocket(tcpPort);
    }

    @Override
    public void initialize() {
        new Registrar(serverSocket, clients, inbox, alive).start();
        eventBus.subscribe(CreatePlayer.class, new PlayerJoinHandler(world, messenger));
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
        }
    }

}
