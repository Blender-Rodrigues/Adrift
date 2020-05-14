package ee.taltech.iti0200.network.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.ConcurrentInbox;
import ee.taltech.iti0200.di.annotations.ConcurrentTcpOutbox;
import ee.taltech.iti0200.di.annotations.ConcurrentUdpOutbox;
import ee.taltech.iti0200.di.annotations.ConnectionAlive;
import ee.taltech.iti0200.di.annotations.ServerHost;
import ee.taltech.iti0200.di.annotations.ServerTcpPort;
import ee.taltech.iti0200.di.factory.ThreadFactory;
import ee.taltech.iti0200.network.Connection;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.PacketObjectInputStream;
import ee.taltech.iti0200.network.PacketObjectOutputStream;
import ee.taltech.iti0200.network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;

public class ConnectionBuilder extends ThreadFactory {

    private final ConcurrentLinkedQueue<Message> inbox;
    private final ConcurrentLinkedQueue<Message> tcpOutbox;
    private final ConcurrentLinkedQueue<Message> udpOutbox;
    private final AtomicBoolean alive;
    private final InetAddress address;
    private final Integer tcpPort;

    private Socket tcpSocket;
    private ObjectInputStream tcpInput;
    private ObjectOutputStream tcpOutput;
    private DatagramSocket udpSocket;
    private ObjectInputStream udpInput;
    private ObjectOutputStream udpOutput;

    @Inject
    public ConnectionBuilder(
        @ServerHost InetAddress address,
        @ServerTcpPort int tcpPort,
        @ConcurrentInbox ConcurrentLinkedQueue<Message> inbox,
        @ConcurrentTcpOutbox ConcurrentLinkedQueue<Message> tcpOutbox,
        @ConcurrentUdpOutbox ConcurrentLinkedQueue<Message> udpOutbox,
        @ConnectionAlive AtomicBoolean alive
    ) {
        this.address = address;
        this.tcpPort = tcpPort;
        this.inbox = inbox;
        this.tcpOutbox = tcpOutbox;
        this.udpOutbox = udpOutbox;
        this.alive = alive;
    }

    public Socket createTcpSocket() throws IOException {
        tcpSocket = new Socket(address, tcpPort);
        return tcpSocket;
    }

    public ObjectOutputStream createTcpOutput() throws IOException {
        tcpOutput = new ObjectOutputStream(tcpSocket.getOutputStream());
        return tcpOutput;
    }

    public ObjectInputStream createTcpInput() throws IOException {
        tcpInput = new ObjectInputStream(tcpSocket.getInputStream());
        return tcpInput;
    }

    public DatagramSocket createUdpSocket() throws SocketException {
        udpSocket = new DatagramSocket();
        return udpSocket;
    }

    public ObjectOutputStream createUdpOutput(int port) throws IOException {
        udpOutput = new PacketObjectOutputStream(udpSocket, address, port);
        return udpOutput;
    }

    public ObjectInputStream createUdpInput() throws IOException {
        udpInput = new PacketObjectInputStream(udpSocket);
        return udpInput;
    }

    public List<Thread> createConnectionThreads(Connection connection) {
        Messenger tcpMessenger = new Messenger(inbox, tcpOutbox, alive);
        Messenger udpMessenger = new Messenger(inbox, udpOutbox, alive);

        return asList(
            createSender("Client TCP", tcpOutput, tcpMessenger, connection),
            createSender("Client UDP", udpOutput, udpMessenger, connection),
            createListener("Client TCP", tcpInput, tcpMessenger, connection),
            createListener("Client UDP", udpInput, udpMessenger, connection)
        );
    }

}
