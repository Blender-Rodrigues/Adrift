package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

import java.io.Serializable;

public interface Message extends Serializable {

    Protocol getChannel();
    Receiver getReceiver();

}
