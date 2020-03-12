package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

public class TcpRegistrationResponse implements Message {

    private int udpPort;

    public TcpRegistrationResponse(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
