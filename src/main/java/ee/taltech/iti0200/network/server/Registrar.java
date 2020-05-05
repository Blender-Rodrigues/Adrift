package ee.taltech.iti0200.network.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.ConnectionAlive;
import ee.taltech.iti0200.di.annotations.ServerClients;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.network.message.TcpRegistrationRequest;
import ee.taltech.iti0200.network.message.TcpRegistrationResponse;
import ee.taltech.iti0200.network.message.UdpRegistrationRequest;
import ee.taltech.iti0200.network.message.UdpRegistrationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Registrar extends Thread {

    private final Logger logger = LogManager.getLogger(Registrar.class);
    private final ServerSocket serverSocket;
    private final Set<ConnectionToClient> clients;
    private final AtomicBoolean alive;
    private final ServerThreadFactory factory;

    @Inject
    public Registrar(
        ServerSocket serverSocket,
        @ServerClients Set<ConnectionToClient> clients,
        @ConnectionAlive AtomicBoolean alive,
        ServerThreadFactory factory
    ) {
        this.serverSocket = serverSocket;
        this.clients = clients;
        this.alive = alive;
        this.factory = factory;
        setName("Server registrar");
    }

    /**
     * Waits and registers new client connections.
     */
    public void run() {
        while (alive.get()) {
            try {
                Socket socket;
                synchronized(serverSocket) {
                    socket = serverSocket.accept();
                }

                InetAddress address = socket.getInetAddress();
                int port = socket.getPort();

                ConnectionToClient connection = new ConnectionToClient(address, port);

                if (clients.contains(connection)) {
                    logger.warn("Client connection {}:{} already existing.", address.getHostName(), port);
                } else {
                    logger.info("Connection accepted from {}:{} ", address.getHostName(), port);
                    register(factory.getBuilderFor(socket, connection), connection);
                }
            } catch (IOException e) {
                logger.error("I/O error: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Creates separate sockets and threads for handling per client TCP and UDP communication.
     * Handles initial client communication setup.
     */
    private void register(ConnectionBuilder builder, ConnectionToClient connection) throws IOException {
        ObjectInputStream tcpInput = builder.createTcpInput();
        ObjectOutputStream tcpOutput = builder.createTcpOutput();
        tcpOutput.flush();

        DatagramSocket udpSocket = builder.createUdpSocket();
        Integer udpPort = udpSocket.getLocalPort();

        connection.setTcpSocket(builder.getTcpSocket())
            .setTcpInput(tcpInput)
            .setTcpOutput(tcpOutput)
            .setUdpSocket(udpSocket)
            .setUdpPort(udpPort);

        Map<Class<? extends Message>, Consumer<Message>> udpHandlers = new HashMap<>();
        udpHandlers.put(UdpRegistrationRequest.class, (message) -> {
            UUID id = connection.getId();

            if (connection.isFinalized()) {
                logger.warn("Re-responding to client {} UdpRegistrationRequest", id);
            } else {
                logger.info("Responding to client {} UdpRegistrationRequest", id);
            }

            try {
                ObjectOutputStream udpOutput = connection.getUdpOutput();
                udpOutput.writeObject(new UdpRegistrationResponse(new Receiver(id)));
                udpOutput.flush();
                connection.finalized();
                clients.add(connection);
            } catch (IOException e) {
                logger.error("Failed to respond to registration request " + e.getMessage(), e);
            }
        });

        Map<Class<? extends Message>, Consumer<Message>> tcpHandlers = new HashMap<>();

        tcpHandlers.put(TcpRegistrationRequest.class, (message) -> {
            TcpRegistrationRequest request = (TcpRegistrationRequest) message;
            connection.setId(request.getId());
            logger.info("Responding to client {} with UDP port: {}", request.getId(), udpPort);

            try {
                ObjectInputStream udpInput = builder.createUdpInput();
                ObjectOutputStream udpOutput = builder.createUdpOutput(request.getUdpPort());

                connection.setUdpInput(udpInput).setUdpOutput(udpOutput);

                logger.info("Client UDP port received: " + request.getUdpPort());

                builder.createUdpThreads(udpHandlers).forEach(Thread::start);

                tcpOutput.writeObject(new TcpRegistrationResponse(udpPort, new Receiver(request.getId())));
                tcpOutput.flush();
            } catch (IOException e) {
                logger.error("Failed to respond to client register request: " + e.getMessage(), e);
            }
        });

        builder.createTcpThreads(tcpHandlers).forEach(Thread::start);
    }

}
