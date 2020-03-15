package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.network.message.Message;

import java.util.UUID;

public class CreatePlayer extends CreateEntity implements Message {

    public CreatePlayer(Entity entity) {
        super(entity);
    }

    @Override
    public boolean deliverTo(UUID id) {
        return !this.getId().equals(id);
    }

}
