package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.network.message.Message;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.UUID;

public class RemoveEntity extends Event implements Message {

    private Entity entity;
    private UUID id;

    public RemoveEntity(Entity entity) {
        this.entity = entity;
        this.id = entity.getId();
    }

    public Entity getEntity() {
        return entity;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
