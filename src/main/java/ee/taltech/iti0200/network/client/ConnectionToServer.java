package ee.taltech.iti0200.network.client;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.entity.CreatePlayer;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.network.Connection;
import ee.taltech.iti0200.network.Listener;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.PacketObjectInputStream;
import ee.taltech.iti0200.network.PacketObjectOutputStream;
import ee.taltech.iti0200.network.Sender;
import ee.taltech.iti0200.network.message.LoadWorld;
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
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

public class ConnectionToServer extends Connection {

    private final Logger logger = LogManager.getLogger(ConnectionToServer.class);
    private final ConcurrentLinkedQueue<Message> inbox;
    private final ConcurrentLinkedQueue<Message> tcpOutbox;
    private final ConcurrentLinkedQueue<Message> udpOutbox;
    private final AtomicBoolean alive;
    private final Player player;

    private LoadWorld worldData;

    public ConnectionToServer(
        InetAddress address,
        int tcpPort,
        ConcurrentLinkedQueue<Message> inbox,
        ConcurrentLinkedQueue<Message> tcpOutbox,
        ConcurrentLinkedQueue<Message> udpOutbox,
        AtomicBoolean alive,
        Player player
    ) {
        super(address, tcpPort);
        this.inbox = inbox;
        this.tcpOutbox = tcpOutbox;
        this.udpOutbox = udpOutbox;
        this.alive = alive;
        this.player = player;
    }

    /**
     * Establishes first a TCP and then an UDP connection to the server.
     * Creates separate threads for up and down communications on both protocols.
     */
    public void initialize() throws IOException, ClassNotFoundException {
        udpSocket = new DatagramSocket();

        tcpSocket = new Socket(address, tcpPort);
        tcpOutput = new ObjectOutputStream(tcpSocket.getOutputStream());
        tcpOutput.flush();

        tcpInput = new ObjectInputStream(tcpSocket.getInputStream());

        tcpSocket.setSoTimeout(RETRY);

        TcpRegistrationResponse response = (TcpRegistrationResponse) retry(TcpRegistrationResponse.class, tcpInput, () -> {
            tcpOutput.writeObject(new TcpRegistrationRequest(player.getId(), udpSocket.getLocalPort()));
            tcpOutput.flush();
        });

        logger.info("Received UDP port {} from server", response.getUdpPort());

        udpOutput = new PacketObjectOutputStream(udpSocket, address, response.getUdpPort());

        retry(UdpRegistrationResponse.class, tcpInput, () -> {
            udpOutput.writeObject(new UdpRegistrationRequest());
            logger.debug("Trying to register UDP against port " + response.getUdpPort());
        });

        worldData = (LoadWorld) retry(LoadWorld.class, tcpInput, () -> {
            tcpOutput.writeObject(new CreatePlayer(player, Receiver.SERVER));
            tcpOutput.flush();
        });

        tcpSocket.setSoTimeout(0);

        udpOutput = new PacketObjectOutputStream(udpSocket, address, response.getUdpPort());
        udpInput = new PacketObjectInputStream(udpSocket);

        Messenger tcpMessenger = new Messenger(inbox, tcpOutbox, alive);
        Messenger udpMessenger = new Messenger(inbox, udpOutbox, alive);

        new Sender("Client TCP", tcpOutput, tcpMessenger, this).start();
        new Sender("Client UDP", udpOutput, udpMessenger, this).start();

        new Listener("Client TCP", tcpInput, tcpMessenger, this).start();
        new Listener("Client UDP", udpInput, udpMessenger, this).start();

        finalized();
        logger.info("Connected to " + address.getHostName());
    }

    public LoadWorld getWorldData() {
        return worldData;
    }

    /**
     * Retry sending a message provided by runnable parameter every 3 seconds up to 30 seconds.
     * Break and return when a message of the specified type is received from the server.
     */
    private Message retry(
        Class<? extends Message> type,
        ObjectInputStream stream,
        RelaxedRunnable runnable
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
                logger.debug("Received premature message {} from server", message.getClass());
            }
            now = System.currentTimeMillis();
        }

        throw new RuntimeException(format("Failed to get a response from server in %d ms", TIMEOUT));
    }

    @FunctionalInterface
    public interface RelaxedRunnable {

        void run() throws IOException, ClassNotFoundException;

    }

    @Override
    public String toString() {
        return format("Connection{server, %s:%d}", address.getHostAddress(), tcpPort);
    }

}
