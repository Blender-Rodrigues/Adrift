package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.TcpListener;
import ee.taltech.iti0200.network.TcpSender;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Ping;
import ee.taltech.iti0200.network.message.RegisterClientRequest;
import ee.taltech.iti0200.network.message.RegisterClientResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class TcpRegistrar extends Thread {

    private final Logger logger = LogManager.getLogger(TcpRegistrar.class);
    private final ServerSocket serverSocket;
    private final Set<ClientConnection> clients;
    private final ConcurrentLinkedQueue<Message> inbox;
    private final AtomicBoolean alive;

    public TcpRegistrar(
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

        // TODO: create udp port and thread.
        Integer udpSenderPort = 8885;
        Integer udpListenerPort = 8886;

        connection.setTcpInput(tcpInput)
            .setTcpOutput(tcpOutput)
            .setUdpSenderPort(udpSenderPort)
            .setUdpListenerPort(udpListenerPort);

        Map<Class<? extends Message>, Consumer<Message>> handlers = new HashMap<>();

        handlers.put(RegisterClientRequest.class, (message) -> {
            RegisterClientRequest request = (RegisterClientRequest) message;
            connection.setId(request.getId());
            logger.info(
                "Responding to client {} with ports: {}/{}",
                request.getId(),
                udpSenderPort,
                udpListenerPort
            );

            try {
                tcpOutput.writeObject(new RegisterClientResponse(udpSenderPort, udpListenerPort));
                tcpOutput.flush();
            } catch (IOException e) {
                logger.error("Failed to respond to client register request: " + e.getMessage(), e);
            }
        });

        handlers.put(Ping.class, (message) -> {
            Ping ping = (Ping) message;
            logger.info("Ping received from client {} at {}", ping.getId(), ping.getTime());
            inbox.add(ping);
        });

        Messenger tcpMessenger = new Messenger(inbox, connection.getTcpOutbox(), alive);
        new TcpListener(tcpInput, tcpMessenger, handlers).start();
        new TcpSender(tcpOutput, tcpMessenger).start();
    }

}
