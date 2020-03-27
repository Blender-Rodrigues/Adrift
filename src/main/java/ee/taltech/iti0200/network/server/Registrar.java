package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.network.Listener;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.PacketObjectInputStream;
import ee.taltech.iti0200.network.PacketObjectOutputStream;
import ee.taltech.iti0200.network.Sender;
import ee.taltech.iti0200.network.message.Message;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Registrar extends Thread {

    private final Logger logger = LogManager.getLogger(Registrar.class);
    private final ServerSocket serverSocket;
    private final Set<ConnectionToClient> clients;
    private final ConcurrentLinkedQueue<Message> inbox;
    private final AtomicBoolean alive;

    public Registrar(
        ServerSocket serverSocket,
        Set<ConnectionToClient> clients,
        ConcurrentLinkedQueue<Message> inbox,
        AtomicBoolean alive
    ) {
        this.serverSocket = serverSocket;
        this.clients = clients;
        this.inbox = inbox;
        this.alive = alive;
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
                    register(socket, connection);
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
    private void register(Socket socket, ConnectionToClient connection) throws IOException {
        ObjectInputStream tcpInput = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream tcpOutput = new ObjectOutputStream(socket.getOutputStream());
        tcpOutput.flush();

        DatagramSocket udpSocket = new DatagramSocket();
        Integer udpPort = udpSocket.getLocalPort();

        connection.setTcpSocket(socket)
            .setTcpInput(tcpInput)
            .setTcpOutput(tcpOutput)
            .setUdpSocket(udpSocket)
            .setUdpPort(udpPort);

        Map<Class<? extends Message>, Consumer<Message>> udpHandlers = new HashMap<>();
        udpHandlers.put(UdpRegistrationRequest.class, (message) -> {
            if (connection.isFinalized()) {
                return;
            }

            try {
                tcpOutput.writeObject(new UdpRegistrationResponse(new Receiver(connection.getId())));
                tcpOutput.flush();
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
                ObjectInputStream udpInput = new PacketObjectInputStream(udpSocket);
                ObjectOutputStream udpOutput = new PacketObjectOutputStream(
                    udpSocket,
                    connection.getAddress(),
                    request.getUdpPort()
                );

                connection.setUdpInput(udpInput).setUdpOutput(udpOutput);

                logger.debug("Client UDP port received: " + request.getUdpPort());

                Messenger udpMessenger = new Messenger(inbox, connection.getUdpOutbox(), alive);
                new Listener("Server UDP", udpInput, udpMessenger, connection, udpHandlers).start();
                new Sender("Server UDP", udpOutput, udpMessenger, connection).start();

                tcpOutput.writeObject(new TcpRegistrationResponse(udpPort, new Receiver(request.getId())));
                tcpOutput.flush();
            } catch (IOException e) {
                logger.error("Failed to respond to client register request: " + e.getMessage(), e);
            }
        });

        Messenger tcpMessenger = new Messenger(inbox, connection.getTcpOutbox(), alive);
        new Listener("Server TCP", tcpInput, tcpMessenger, connection, tcpHandlers).start();
        new Sender("Server TCP", tcpOutput, tcpMessenger, connection).start();
    }

}
