package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

import static java.lang.String.format;

public class TcpRegistrationResponse implements Message {

    private int udpPort;
    private Receiver receiver;

    public TcpRegistrationResponse(int udpPort, Receiver receiver) {
        this.udpPort = udpPort;
        this.receiver = receiver;
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
        return receiver;
    }

    @Override
    public String toString() {
        return format("TcpRegistrationResponse{udpPort=%d}", udpPort);
    }

}
