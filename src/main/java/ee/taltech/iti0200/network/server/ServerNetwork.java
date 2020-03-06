package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.domain.World;
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

public class ServerNetwork extends Network {

    private final Logger logger = LogManager.getLogger(ServerNetwork.class);
    private final Set<ClientConnection> clients = ConcurrentHashMap.newKeySet();
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
    }

    @Override
    public void update(long tick) {
        LinkedList<Message> messages = messenger.readInbox();
        messages.forEach(message -> logger.debug(
            "Server received {}: {}",
            message.getClass().getSimpleName(),
            message.toString()
        ));
    }

    @Override
    public void propagate(long tick) {
        if (tick % 400 == 0) {
            LinkedList<Message> messages = new LinkedList<>();
            messages.add(new Ping(tick, id, Protocol.TCP));
            messages.add(new Ping(tick, id, Protocol.UDP));
            messenger.writeOutbox(messages);
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        clients.forEach(ClientConnection::close);
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Failed to close " + serverSocket.getClass() + ": " + e.getMessage(), e);
        }
    }

}
