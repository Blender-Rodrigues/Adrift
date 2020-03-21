package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

import java.util.UUID;

public class TcpRegistrationRequest implements Message {

    private UUID id;
    private int udpPort;

    public TcpRegistrationRequest(UUID id, int udpPort) {
        this.id = id;
        this.udpPort = udpPort;
    }

    public UUID getId() {
        return id;
    }

    public int getUdpPort() {
        return udpPort;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

    @Override
    public Receiver getReceiver() {
        return Receiver.SERVER;
    }

}
