package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.Listener;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.PacketObjectInputStream;
import ee.taltech.iti0200.network.PacketObjectOutputStream;
import ee.taltech.iti0200.network.Sender;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.TcpRegistrationRequest;
import ee.taltech.iti0200.network.message.TcpRegistrationResponse;
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
    private final Set<ClientConnection> clients;
    private final ConcurrentLinkedQueue<Message> inbox;
    private final AtomicBoolean alive;

    public Registrar(
        ServerSocket serverSocket,
        Set<ClientConnection> clients,
        ConcurrentLinkedQueue<Message> inbox,
        AtomicBoolean alive
    ) {
        this.serverSocket = serverSocket;
        this.clients = clients;
        this.inbox = inbox;
        this.alive = alive;
    }

    public void run() {
        while (alive.get()) {
            try {
                Socket socket;
                synchronized(serverSocket) {
                    socket = serverSocket.accept();
                }

                InetAddress address = socket.getInetAddress();
                int port = socket.getPort();

                ClientConnection connection = new ClientConnection(address, port);

                if (clients.contains(connection)) {
                    logger.warn("Client connection {}:{} already existing.", address, port);
                } else {
                    logger.info("Connection accepted from {}:{} ", address.toString(), port);
                    register(socket, connection);
                }
            } catch (IOException e) {
                logger.error("I/O error: " + e.getMessage(), e);
            }
        }
    }

    private void register(Socket socket, ClientConnection connection) throws IOException {
        clients.add(connection);

        ObjectInputStream tcpInput = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream tcpOutput = new ObjectOutputStream(socket.getOutputStream());
        tcpOutput.flush();

        DatagramSocket udpSocket = new DatagramSocket();
        Integer udpPort = udpSocket.getLocalPort();

        connection.setTcpInput(tcpInput)
            .setTcpOutput(tcpOutput)
            .setUdpPort(udpPort);

        Map<Class<? extends Message>, Consumer<Message>> handlers = new HashMap<>();

        handlers.put(TcpRegistrationRequest.class, (message) -> {
            TcpRegistrationRequest request = (TcpRegistrationRequest) message;
            connection.setId(request.getId());
            logger.info("Responding to client {} with UDP port: {}", request.getId(), udpPort);

            try {
                Messenger udpMessenger = new Messenger(inbox, connection.getUdpOutbox(), alive);
                ObjectInputStream udpInput = new PacketObjectInputStream(udpSocket);
                ObjectOutputStream udpOutput = new PacketObjectOutputStream(udpSocket, connection.getAddress(), request.getUdpPort());

                logger.debug("Client UDP port received: " + request.getUdpPort());

                new Listener(udpInput, udpMessenger, new HashMap<>()).start();
                new VerifiedSender(connection, udpOutput, udpMessenger).start();

                connection.setUdpInput(udpInput).setUdpOutput(udpOutput);

                tcpOutput.writeObject(new TcpRegistrationResponse(udpPort));
                tcpOutput.flush();
            } catch (IOException e) {
                logger.error("Failed to respond to client register request: " + e.getMessage(), e);
            }
        });

        Messenger tcpMessenger = new Messenger(inbox, connection.getTcpOutbox(), alive);
        new Listener(tcpInput, tcpMessenger, handlers).start();
        new Sender(tcpOutput, tcpMessenger).start();
    }

}
