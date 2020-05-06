package ee.taltech.iti0200.domain.event.handler.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.factory.LootFactory;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Consumable;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Loot;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.DropLoot;

import java.util.HashMap;
import java.util.Map;

import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;

public class DropLootHandler implements Subscriber<DropLoot> {

    private World world;
    private LootFactory lootFactory;
    private EventBus eventBus;
    private Map<Living, Loot> loots = new HashMap<>();

    @Inject
    public DropLootHandler(World world, LootFactory lootFactory, EventBus eventBus) {
        this.world = world;
        this.lootFactory = lootFactory;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(DropLoot event) {
        if (loots.containsKey(event.getVictim())) {
            return;
        }

        Loot loot = lootFactory.create(event.getLocation());
        eventBus.dispatch(new CreateEntity(loot, EVERYONE));
    }

}
