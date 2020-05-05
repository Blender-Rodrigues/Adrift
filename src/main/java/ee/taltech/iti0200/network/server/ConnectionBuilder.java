package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.di.factory.ThreadFactory;
import ee.taltech.iti0200.network.Messenger;
import ee.taltech.iti0200.network.PacketObjectInputStream;
import ee.taltech.iti0200.network.PacketObjectOutputStream;
import ee.taltech.iti0200.network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class ConnectionBuilder extends ThreadFactory {

    private final ConcurrentLinkedQueue<Message> inbox;
    private final AtomicBoolean alive;
    private final Socket tcpSocket;
    private final ConnectionToClient connection;

    private ObjectInputStream tcpInput;
    private ObjectOutputStream tcpOutput;
    private DatagramSocket udpSocket;
    private ObjectInputStream udpInput;
    private ObjectOutputStream udpOutput;

    public ConnectionBuilder(
        Socket tcpSocket,
        ConcurrentLinkedQueue<Message> inbox,
        AtomicBoolean alive,
        ConnectionToClient connection
    ) {
        this.tcpSocket = tcpSocket;
        this.inbox = inbox;
        this.alive = alive;
        this.connection = connection;
    }

    public Socket getTcpSocket() {
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
        udpOutput = new PacketObjectOutputStream(udpSocket, connection.getAddress(), port);
        return udpOutput;
    }

    public ObjectInputStream createUdpInput() throws IOException {
        udpInput = new PacketObjectInputStream(udpSocket);
        return udpInput;
    }

    public List<Thread> createTcpThreads(Map<Class<? extends Message>, Consumer<Message>> handlers) {
        Messenger messenger = new Messenger(inbox, connection.getTcpOutbox(), alive);

        return asList(
            createListener("Server TCP", tcpInput, messenger, connection, handlers),
            createSender("Server TCP", tcpOutput, messenger, connection)
        );
    }

    public List<Thread> createUdpThreads(Map<Class<? extends Message>, Consumer<Message>> handlers) {
        Messenger udpMessenger = new Messenger(inbox, connection.getUdpOutbox(), alive);

        return asList(
            createListener("Server UDP", udpInput, udpMessenger, connection, handlers),
            createSender("Server UDP", udpOutput, udpMessenger, connection)
        );
    }

}
