package ee.taltech.iti0200.application;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.FastGun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.domain.event.handler.CollisionHandler;
import ee.taltech.iti0200.domain.event.handler.EntityCreateHandler;
import ee.taltech.iti0200.domain.event.handler.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.handler.EntityRemoveHandler;
import ee.taltech.iti0200.domain.event.handler.MoveBodyHandler;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.physics.Physics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientGame extends Game {

    private final Logger logger = LogManager.getLogger(ClientGame.class);

    private Player player;
    private Graphics graphics;
    private Network network;

    @Inject
    public ClientGame(
        World world,
        EventBus eventBus,
        Timer timer,
        @LocalPlayer Player player,
        Graphics graphics,
        Network network,
        Input input,
        Physics physics,
        EntityDamageHandler damageHandler,
        EntityRemoveHandler entityRemoveHandler,
        EntityCreateHandler entityCreateHandler,
        MoveBodyHandler moveBodyHandler,
        CollisionHandler collisionHandler
    ) {
        super(world, eventBus, timer);
        this.player = player;
        this.graphics = graphics;
        this.network = network;

        components.add(physics);
        components.add(graphics);
        components.add(input);
        components.add(network);

        eventBus.subscribe(DealDamage.class, damageHandler);
        eventBus.subscribe(RemoveEntity.class, entityRemoveHandler);
        eventBus.subscribe(CreateEntity.class, entityCreateHandler);
        eventBus.subscribe(UpdateVector.class, moveBodyHandler);
        eventBus.subscribe(EntityCollide.class, collisionHandler);
    }

    @Override
    protected void initialize() {
        world.addEntity(player);
        player.setGun(new FastGun(player.getBoundingBox()));

        logger.info("Added {} to the world", player);
    }

    @Override
    protected void loop(long tick) {
        network.propagate(tick);
    }

    @Override
    protected boolean isGameRunning() {
        return graphics.isWindowOpen();
    }

}
