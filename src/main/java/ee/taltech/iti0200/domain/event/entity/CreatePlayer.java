package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;

public class CreatePlayer extends CreateEntity implements Message {

    private static final long serialVersionUID = 1L;

    public CreatePlayer(Entity entity, Receiver receiver) {
        super(entity, receiver);
    }

}
