package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.physics.Vector;
import org.apache.logging.log4j.core.net.Protocol;

import java.util.UUID;

public class CreateEntity extends Event implements Message {

    private Class<? extends Entity> type;
    private UUID id;
    private Vector position;
    private Vector speed;

    public CreateEntity(Entity entity) {
        this.type = entity.getClass();
        this.id = entity.getId();
        this.position = entity.getBoundingBox().getCentre();
        this.speed = entity.getSpeed();
    }

    public Class<? extends Entity> getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getSpeed() {
        return speed;
    }

    @Override
    public Protocol getChannel() {
        return Protocol.TCP;
    }

}
