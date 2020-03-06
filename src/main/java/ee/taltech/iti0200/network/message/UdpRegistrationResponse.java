package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

public class UdpRegistrationResponse implements Message {

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
