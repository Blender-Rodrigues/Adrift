package ee.taltech.iti0200.application;

import com.google.inject.Inject;
import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.Layout;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.FastGun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.entity.HealDamage;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.domain.event.handler.CollisionHandler;
import ee.taltech.iti0200.domain.event.handler.DropLootHandler;
import ee.taltech.iti0200.domain.event.handler.EntityCreateHandler;
import ee.taltech.iti0200.domain.event.handler.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.handler.EntityHealingHandler;
import ee.taltech.iti0200.domain.event.handler.EntityRemoveHandler;
import ee.taltech.iti0200.domain.event.handler.GunShotHandler;
import ee.taltech.iti0200.domain.event.handler.MoveBodyHandler;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.physics.Physics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SinglePlayerGame extends Game {

    private final Logger logger = LogManager.getLogger(SinglePlayerGame.class);

    private Player player;
    private Layout layout;
    private Graphics graphics;
    private Score score;

    @Inject
    public SinglePlayerGame(
        World world,
        EventBus eventBus,
        Timer timer,
        @LocalPlayer Player player,
        Layout layout,
        Graphics graphics,
        Input input,
        Physics physics,
        Intelligence ai,
        GunShotHandler gunShotHandler,
        EntityDamageHandler damageHandler,
        EntityHealingHandler healingHandler,
        EntityRemoveHandler entityRemoveHandler,
        EntityCreateHandler entityCreateHandler,
        DropLootHandler dropLootHandler,
        MoveBodyHandler moveBodyHandler,
        CollisionHandler collisionHandler,
        Score score
    ) {
        super(world, eventBus, timer);

        this.player = player;
        this.layout = layout;
        this.graphics = graphics;

        this.score = score;

        components.add(graphics);
        components.add(input);
        components.add(physics);
        components.add(ai);

        eventBus.subscribe(GunShot.class, gunShotHandler);
        eventBus.subscribe(DealDamage.class, damageHandler);
        eventBus.subscribe(HealDamage.class, healingHandler);
        eventBus.subscribe(RemoveEntity.class, entityRemoveHandler);
        eventBus.subscribe(CreateEntity.class, entityCreateHandler);
        eventBus.subscribe(DropLoot.class, dropLootHandler);
        eventBus.subscribe(UpdateVector.class, moveBodyHandler);
        eventBus.subscribe(EntityCollide.class, collisionHandler);
    }

    @Override
    protected void initialize() throws IOException {
        layout.populateWorld(world);
        world.initialize();

        player.setPosition(world.nextPlayerSpawnPoint());
        player.setGun(new FastGun(player.getBoundingBox()));

        world.addEntity(player);
        logger.info("Added {} to the world", player);

        score.addPlayer(player);
    }

    @Override
    protected void loop(long tick) {
        eventBus.propagateAll();
    }

    @Override
    protected boolean isGameRunning() {
        return graphics.isWindowOpen();
    }

}
