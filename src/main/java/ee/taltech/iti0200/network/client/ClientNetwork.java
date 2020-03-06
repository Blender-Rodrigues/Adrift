package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.network.Listener;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.PacketObjectInputStream;
import ee.taltech.iti0200.network.PacketObjectOutputStream;
import ee.taltech.iti0200.network.Sender;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Ping;
import ee.taltech.iti0200.network.message.TcpRegistrationRequest;
import ee.taltech.iti0200.network.message.TcpRegistrationResponse;
import ee.taltech.iti0200.network.message.UdpRegistrationRequest;
import ee.taltech.iti0200.network.message.UdpRegistrationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;

public class ClientNetwork extends Network {

    private static final int TIMEOUT = 30000;
    private static final int RETRY = 3000;

    private final Logger logger = LogManager.getLogger(ClientNetwork.class);
    private final Messenger messenger = new Messenger();
    private final UUID id = UUID.randomUUID();
    private final String host;
    private final Integer tcpPort;

    private Socket tcpSocket;
    private ObjectInputStream tcpInput;
    private ObjectOutputStream tcpOutput;
    private DatagramSocket udpSocket;
    private ObjectInputStream udpInput;
    private ObjectOutputStream udpOutput;

    public ClientNetwork(World world, String host, Integer tcpPort) {
        super(world);
        this.host = host;
        this.tcpPort = tcpPort;
    }

    @Override
    public void initialize() throws IOException, ClassNotFoundException {
        udpSocket = new DatagramSocket();

        tcpSocket = new Socket(InetAddress.getByName(host), tcpPort);
        tcpOutput = new ObjectOutputStream(tcpSocket.getOutputStream());
        tcpOutput.flush();

        tcpInput = new ObjectInputStream(tcpSocket.getInputStream());

        tcpSocket.setSoTimeout(RETRY);

        TcpRegistrationResponse response = (TcpRegistrationResponse) retry(TcpRegistrationResponse.class, tcpInput, () -> {
            try {
                tcpOutput.writeObject(new TcpRegistrationRequest(id, udpSocket.getLocalPort()));
                tcpOutput.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("Received sender/listener UDP ports from server: {}", response.getUdpPort());

        udpOutput = new PacketObjectOutputStream(udpSocket, InetAddress.getByName(host), response.getUdpPort());

        retry(UdpRegistrationResponse.class, tcpInput, () -> {
            try {
                udpOutput.writeObject(new UdpRegistrationRequest());
                logger.debug("Trying to register UDP against port " + response.getUdpPort());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        tcpSocket.setSoTimeout(0);

        udpOutput = new PacketObjectOutputStream(udpSocket, InetAddress.getByName(host), response.getUdpPort());
        udpInput = new PacketObjectInputStream(udpSocket);

        new Sender(tcpOutput, messenger).start();
        new Sender(udpOutput, messenger).start();

        Map<Class<? extends Message>, Consumer<Message>> handlers = new HashMap<>();

        new Listener(tcpInput, messenger, handlers).start();
        new Listener(udpInput, messenger, handlers).start();
    }

    @Override
    public void update(long tick) {
        LinkedList<Message> messages = messenger.readInbox();
        messages.forEach(message -> logger.debug(
            "Client received {}: {}",
            message.getClass().getSimpleName(),
            message.toString()
        ));
    }

    @Override
    public void propagate(long tick) {
        Thread.yield();
        if (tick % 300 == 0) {
            LinkedList<Message> messages = new LinkedList<>();

            messages.add(new Ping(tick, id, Protocol.TCP));
            messages.add(new Ping(tick, id, Protocol.UDP));

            messenger.writeOutbox(messages);
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        Stream.of(tcpInput, tcpOutput, tcpSocket)
            .forEach(closeable -> {
                try {
                    closeable.close();
                } catch (IOException e) {
                    logger.error("Failed to close " + closeable.getClass() + " with: " + e.getMessage(), e);
                }
            });
    }

    private Message retry(
        Class<? extends Message> type,
        ObjectInputStream stream,
        Runnable runnable
    ) throws IOException, ClassNotFoundException {
        runnable.run();

        long now = System.currentTimeMillis();
        long until = System.currentTimeMillis() + TIMEOUT;

        while (now < until) {
            Object message;
            try {
                message = stream.readObject();
            } catch (SocketTimeoutException exception) {
                now = System.currentTimeMillis();
                runnable.run();
                continue;
            }
            if (type.isInstance(message)) {
                return type.cast(message);
            } else {
                logger.debug("Received unexpected message {} from server", message.getClass());
            }
            now = System.currentTimeMillis();
        }

        throw new RuntimeException(format("Failed to get a response from server in %d ms", TIMEOUT));
    }

}
