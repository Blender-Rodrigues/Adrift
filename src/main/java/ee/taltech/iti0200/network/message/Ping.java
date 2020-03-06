package ee.taltech.iti0200.network.message;

import org.apache.logging.log4j.core.net.Protocol;

import java.util.UUID;

public class Ping implements Message {

    private Protocol channel;
    private long time;
    private UUID id;

    public Ping(long time, UUID id, Protocol channel) {
        this.time = time;
        this.id = id;
        this.channel = channel;
    }

    public long getTime() {
        return time;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Protocol getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return String.format("Ping from %s received at %s over %s", id, time, channel.name());
    }

}
