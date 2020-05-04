package ee.taltech.iti0200.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;

abstract public class Connection {

    public static final int TIMEOUT = 30000;
    public static final int RETRY = 3000;

    private final Logger logger = LogManager.getLogger(Connection.class);
    private final AtomicBoolean finalized = new AtomicBoolean(false);

    protected final InetAddress address;

    protected Socket tcpSocket;
    protected Integer tcpPort;
    protected ObjectInputStream tcpInput;
    protected ObjectOutputStream tcpOutput;

    protected DatagramSocket udpSocket;
    protected Integer udpPort;
    protected ObjectInputStream udpInput;
    protected ObjectOutputStream udpOutput;

    public Connection(InetAddress address, int tcpPort) {
        this.address = address;
        this.tcpPort = tcpPort;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Connection setTcpSocket(Socket tcpSocket) {
        this.tcpSocket = tcpSocket;
        return this;
    }

    public Connection setTcpInput(ObjectInputStream tcpInput) {
        this.tcpInput = tcpInput;
        return this;
    }

    public ObjectOutputStream getTcpOutput() {
        return tcpOutput;
    }

    public ObjectOutputStream getUdpOutput() {
        return udpOutput;
    }

    public Connection setTcpOutput(ObjectOutputStream tcpOutput) {
        this.tcpOutput = tcpOutput;
        return this;
    }

    public Connection setUdpSocket(DatagramSocket udpSocket) {
        this.udpSocket = udpSocket;
        return this;
    }

    public Connection setUdpPort(Integer udpPort) {
        this.udpPort = udpPort;
        return this;
    }

    public ObjectInputStream getUdpInput() {
        return udpInput;
    }

    public Connection setUdpInput(ObjectInputStream udpInput) {
        this.udpInput = udpInput;
        return this;
    }

    public Connection setUdpOutput(ObjectOutputStream udpOutput) {
        this.udpOutput = udpOutput;
        return this;
    }

    public void finalized() {
        finalized.set(true);
    }

    public boolean isFinalized() {
        return finalized.get();
    }

    public synchronized boolean isOpen() {
        if (!finalized.get()) {
            return true;
        }
        return !tcpSocket.isClosed() && !udpSocket.isClosed();
    }

    public synchronized void close() {
        asList(tcpSocket, tcpInput, tcpOutput, udpInput, udpOutput, udpSocket).forEach(closeable -> {
            if (closeable == null) {
                return;
            }
            try {
                closeable.close();
            } catch (IOException e) {
                logger.error("Failed to close " + closeable.getClass() + " with: " + e.getMessage(), e);
            }
        });
    }

}
