package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.CreatePlayer;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Ping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static ee.taltech.iti0200.application.Game.eventBus;

public class ServerNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ServerNetwork.class);
    private final Set<ConnectionToClient> clients = ConcurrentHashMap.newKeySet();
    private final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final Messenger messenger = new GroupMessenger(clients, inbox, alive);
    private final UUID id = UUID.randomUUID();

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
    public void update(long tick) {
        LinkedList<Message> messages = messenger.readInbox();

        messages.forEach(message -> {
            if (message instanceof Event) {
                eventBus.dispatch((Event) message);
            } else {
                logger.debug(
                    "Server received non-event {}: {}",
                    message.getClass().getSimpleName(),
                    message.toString()
                );
            }
        });
    }

    @Override
    public void propagate(long tick) {
        Thread.yield();

        // TODO: temporary generic example of sending a message
        if (tick % 1000 == 0) {
            LinkedList<Message> messages = new LinkedList<>();
            messages.add(new Ping(tick, id, Protocol.TCP));
            messages.add(new Ping(tick, id, Protocol.UDP));
            messenger.writeOutbox(messages);
        }
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

}
