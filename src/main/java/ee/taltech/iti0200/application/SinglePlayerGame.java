package ee.taltech.iti0200.application;

import com.google.inject.Inject;
import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.Layout;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.FastGun;
import ee.taltech.iti0200.domain.entity.Gun;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.SpecialGun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.UpdateScore;
import ee.taltech.iti0200.domain.event.client.UpdateScoreHandler;
import ee.taltech.iti0200.domain.event.common.PlayerRespawnHandler;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
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
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.physics.Physics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SinglePlayerGame extends Game {

    private final Logger logger = LogManager.getLogger(SinglePlayerGame.class);

    private final Player player;
    private final Layout layout;
    private final Graphics graphics;
    private final Score score;

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
        PlayerRespawnHandler respawnHandler,
        UpdateScoreHandler scoreHandler,
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
        eventBus.subscribe(Heal.class, healingHandler);
        eventBus.subscribe(RemoveEntity.class, entityRemoveHandler);
        eventBus.subscribe(CreateEntity.class, entityCreateHandler);
        eventBus.subscribe(DropLoot.class, dropLootHandler);
        eventBus.subscribe(UpdateVector.class, moveBodyHandler);
        eventBus.subscribe(EntityCollide.class, collisionHandler);
        eventBus.subscribe(RespawnPlayer.class, respawnHandler);
        eventBus.subscribe(UpdateScore.class, scoreHandler);
    }

    @Override
    protected void initialize() throws IOException {
        layout.populateWorld(world);
        world.initialize();

        player.setPosition(world.nextPlayerSpawnPoint());
        player.addWeapon(new Gun(player.getBoundingBox()));
        player.addWeapon(new FastGun(player.getBoundingBox()));
        player.addWeapon(new SpecialGun(player.getBoundingBox()));
        player.setActiveGun(0);

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
