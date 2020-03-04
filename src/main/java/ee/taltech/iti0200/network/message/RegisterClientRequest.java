package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

import java.util.UUID;

public class RegisterClientRequest implements Message {

    private UUID id;

    public RegisterClientRequest(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
