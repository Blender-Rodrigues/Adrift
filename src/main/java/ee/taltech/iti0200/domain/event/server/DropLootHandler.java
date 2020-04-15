package ee.taltech.iti0200.domain.event.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.factory.ConsumableFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.DropLoot;

import java.util.HashMap;
import java.util.Map;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class DropLootHandler implements Subscriber<DropLoot> {

    private World world;
    private ConsumableFactory consumableFactory;
    private EventBus eventBus;
    private Map<Living, Consumable> loots = new HashMap<>();

    @Inject
    public DropLootHandler(World world, ConsumableFactory consumableFactory, EventBus eventBus) {
        this.world = world;
        this.consumableFactory = consumableFactory;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(DropLoot event) {
        if (loots.containsKey(event.getVictim())) {
            return;
        }

        Consumable consumable = consumableFactory.create(event.getVictim().getBoundingBox().getCentre());
        eventBus.dispatch(new CreateEntity(consumable, EVERYONE));
    }

}
