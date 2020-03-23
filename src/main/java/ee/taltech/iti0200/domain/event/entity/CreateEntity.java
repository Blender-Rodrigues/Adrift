package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Receiver;

import java.util.UUID;

import static java.lang.String.format;

public class CreateEntity extends Event {

    private Entity entity;
    private UUID id;

    public CreateEntity(Entity entity, Receiver receiver) {
        super(receiver);
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
    public String toString() {
        return format("CreateEntity{%s}", entity);
    }

}
