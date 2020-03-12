package ee.taltech.iti0200.network.server;

import ee.taltech.iti0200.network.Connection;
import ee.taltech.iti0200.network.message.Message;

import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionToClient extends Connection {

    private final ConcurrentLinkedQueue<Message> tcpOutbox = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> udpOutbox = new ConcurrentLinkedQueue<>();

    private UUID id;

    public ConnectionToClient(InetAddress address, int tcpPort) {
        super(address, tcpPort);
    }

    public ConcurrentLinkedQueue<Message> getTcpOutbox() {
        return tcpOutbox;
    }

    public ConcurrentLinkedQueue<Message> getUdpOutbox() {
        return udpOutbox;
    }

    public UUID getId() {
        return id;
    }

    public ConnectionToClient setId(UUID id) {
        this.id = id;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, tcpPort);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!getClass().isInstance(other)) {
            return false;
        }
        ConnectionToClient connection = (ConnectionToClient) other;
        return Objects.equals(address, connection.address) && Objects.equals(tcpPort, connection.tcpPort);
    }

}
