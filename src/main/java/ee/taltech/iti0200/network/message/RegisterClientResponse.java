package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

public class RegisterClientResponse implements Message {

    private int udpSenderPort;
    private int udpListenerPort;

    public RegisterClientResponse(int udpSenderPort, int udpListenerPort) {
        this.udpSenderPort = udpSenderPort;
        this.udpListenerPort = udpListenerPort;
    }

    public int getUdpSenderPort() {
        return udpSenderPort;
    }

    public int getUdpListenerPort() {
        return udpListenerPort;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
