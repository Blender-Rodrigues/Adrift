package ee.taltech.iti0200.application;

import com.google.inject.Inject;
import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.domain.Layout;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.common.ChangeEquipmentHandler;
import ee.taltech.iti0200.domain.event.common.PlayerRespawnHandler;
import ee.taltech.iti0200.domain.event.entity.ChangeEquipment;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.CreatePlayer;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.entity.Heal;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.RespawnPlayer;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.domain.event.common.CollisionHandler;
import ee.taltech.iti0200.domain.event.server.DropLootHandler;
import ee.taltech.iti0200.domain.event.common.EntityCreateHandler;
import ee.taltech.iti0200.domain.event.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.common.EntityHealingHandler;
import ee.taltech.iti0200.domain.event.common.EntityRemoveHandler;
import ee.taltech.iti0200.domain.event.server.GunShotHandler;
import ee.taltech.iti0200.domain.event.common.MoveBodyHandler;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.network.server.PlayerJoinHandler;
import ee.taltech.iti0200.network.server.ServerNetwork;
import ee.taltech.iti0200.physics.Physics;

public class ServerGame extends Game {

    private final Layout layout;
    private final Network network;

    @Inject
    public ServerGame(
        World world,
        Timer timer,
        EventBus eventBus,
        Layout layout,
        ServerNetwork network,
        Physics physics,
        Intelligence ai,
        GunShotHandler gunShotHandler,
        EntityDamageHandler damageHandler,
        EntityHealingHandler healingHandler,
        EntityRemoveHandler entityRemoveHandler,
        EntityCreateHandler entityCreateHandler,
        DropLootHandler dropLootHandler,
        MoveBodyHandler moveBodyHandler,
        PlayerJoinHandler playerJoinHandler,
        CollisionHandler collisionHandler,
        PlayerRespawnHandler respawnHandler,
        ChangeEquipmentHandler equipmentHandler
    ) {
        super(world, eventBus, timer);
        this.layout = layout;
        this.network = network;

        components.add(physics);
        components.add(network);
        components.add(ai);

        eventBus.subscribe(GunShot.class, gunShotHandler);
        eventBus.subscribe(DealDamage.class, damageHandler);
        eventBus.subscribe(Heal.class, healingHandler);
        eventBus.subscribe(RemoveEntity.class, entityRemoveHandler);
        eventBus.subscribe(CreateEntity.class, entityCreateHandler);
        eventBus.subscribe(DropLoot.class, dropLootHandler);
        eventBus.subscribe(UpdateVector.class, moveBodyHandler);
        eventBus.subscribe(CreatePlayer.class, playerJoinHandler);
        eventBus.subscribe(EntityCollide.class, collisionHandler);
        eventBus.subscribe(RespawnPlayer.class, respawnHandler);
        eventBus.subscribe(ChangeEquipment.class, equipmentHandler);
    }

    @Override
    protected void initialize() throws Exception {
        layout.populateWorld(world);
        world.initialize();
    }

    @Override
    protected void loop(long tick) {
        network.propagate(tick);
    }

    @Override
    protected boolean isGameRunning() {
        return true;
    }

}
