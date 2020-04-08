package ee.taltech.iti0200.domain.event.handler;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.factory.ConsumableFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.Entity;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class DropLootHandler implements Subscriber<DropLoot> {

    private final Logger logger = LogManager.getLogger(DropLootHandler.class);

    private World world;
    private ConsumableFactory consumableFactory;
    private EventBus eventBus;

    @Inject
    public DropLootHandler(World world, ConsumableFactory consumableFactory, EventBus eventBus) {
        this.world = world;
        this.consumableFactory = consumableFactory;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(DropLoot event) {
        Consumable consumable = consumableFactory.create();
        eventBus.dispatch(new CreateEntity(consumable, EVERYONE));
    }

}
