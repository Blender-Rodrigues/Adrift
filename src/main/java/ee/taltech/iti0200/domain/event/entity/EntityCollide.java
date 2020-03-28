package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;

public class EntityCollide extends Event implements Message {

    private final Entity entity;
    private final Entity other;

    public EntityCollide(Entity entity, Entity other, Receiver receiver) {
        super(receiver);
        this.entity = entity;
        this.other = other;
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity getOther() {
        return other;
    }

}
