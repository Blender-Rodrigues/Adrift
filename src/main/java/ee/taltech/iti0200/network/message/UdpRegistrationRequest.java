package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

public class UdpRegistrationRequest implements Message {

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

    @Override
    public Receiver getReceiver() {
        return Receiver.SERVER;
    }

    @Override
    public String toString() {
        return "UdpRegistrationRequest{}";
    }

}
