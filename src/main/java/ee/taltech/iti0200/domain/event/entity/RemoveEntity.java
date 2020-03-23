package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.UUID;

import static java.lang.String.format;

public class RemoveEntity extends Event implements Message {

    private UUID id;

    public RemoveEntity(Entity entity, Receiver receiver) {
        this(entity.getId(), receiver);
    }

    public RemoveEntity(UUID id, Receiver receiver) {
        super(receiver);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

    @Override
    public String toString() {
        return format("RemoveEntity{%s}", id);
    }

}
