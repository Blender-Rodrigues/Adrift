package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

import java.util.UUID;

public class Ping implements Message {

    private long time;
    private UUID id;

    public Ping(long time, UUID id) {
        this.time = time;
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
