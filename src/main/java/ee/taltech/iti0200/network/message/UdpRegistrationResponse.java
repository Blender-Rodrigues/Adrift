package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

public class UdpRegistrationResponse implements Message {

    private Receiver receiver;

    public UdpRegistrationResponse(Receiver receiver) {
        this.receiver = receiver;
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
        return "UdpRegistrationResponse{}";
    }

}
