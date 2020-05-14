package ee.taltech.iti0200.application;

import ee.taltech.iti0200.ai.Intelligence;
import ee.taltech.iti0200.domain.Layout;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.handler.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.handler.client.MatchRestartHandler;
import ee.taltech.iti0200.domain.event.handler.client.PlayerRespawnHandler;
import ee.taltech.iti0200.domain.event.handler.client.UpdateScoreHandler;
import ee.taltech.iti0200.domain.event.handler.common.ChangeEquipmentHandler;
import ee.taltech.iti0200.domain.event.handler.common.CollisionHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityCreateHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityGiveGunHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityHealingHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityRemoveHandler;
import ee.taltech.iti0200.domain.event.handler.common.MoveBodyHandler;
import ee.taltech.iti0200.domain.event.handler.server.DropLootHandler;
import ee.taltech.iti0200.domain.event.handler.server.GunShotHandler;
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.GameInput;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.physics.Physics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SinglePlayerGameTest {

    World world;
    EventBus eventBus;
    Timer timer;
    Player player;
    Layout layout;
    Graphics graphics;
    Network network;
    GameInput input;
    Physics physics;
    Intelligence ai;
    EntityDamageHandler damageHandler;
    EntityHealingHandler healingHandler;
    EntityGiveGunHandler entityGiveGunHandler;
    EntityRemoveHandler entityRemoveHandler;
    EntityCreateHandler entityCreateHandler;
    DropLootHandler dropLootHandler;
    MoveBodyHandler moveBodyHandler;
    CollisionHandler collisionHandler;
    PlayerRespawnHandler respawnHandler;
    UpdateScoreHandler scoreHandler;
    GunShotHandler gunShotHandler;
    ChangeEquipmentHandler equipmentHandler;
    MatchRestartHandler restartHandler;
    Score score;

    private SinglePlayerGame game;

    @BeforeEach
    void setUp() {
        world = mock(World.class);
        eventBus = mock(EventBus.class);
        timer = mock(Timer.class);
        player = mock(Player.class);
        layout = mock(Layout.class);
        graphics = mock(Graphics.class);
        network = mock(Network.class);
        input = mock(GameInput.class);
        physics = mock(Physics.class);
        ai = mock(Intelligence.class);
        damageHandler = mock(EntityDamageHandler.class);
        healingHandler = mock(EntityHealingHandler.class);
        entityGiveGunHandler = mock(EntityGiveGunHandler.class);
        entityRemoveHandler = mock(EntityRemoveHandler.class);
        entityCreateHandler = mock(EntityCreateHandler.class);
        dropLootHandler = mock(DropLootHandler.class);
        moveBodyHandler = mock(MoveBodyHandler.class);
        collisionHandler = mock(CollisionHandler.class);
        respawnHandler = mock(PlayerRespawnHandler.class);
        scoreHandler = mock(UpdateScoreHandler.class);
        gunShotHandler = mock(GunShotHandler.class);
        equipmentHandler = mock(ChangeEquipmentHandler.class);
        restartHandler = mock(MatchRestartHandler.class);
        score = mock(Score.class);

        game = new SinglePlayerGame(
            world,
            eventBus,
            timer,
            player,
            layout,
            graphics,
            input,
            physics,
            ai,
            gunShotHandler,
            damageHandler,
            healingHandler,
            entityGiveGunHandler,
            entityRemoveHandler,
            entityCreateHandler,
            dropLootHandler,
            moveBodyHandler,
            collisionHandler,
            scoreHandler,
            equipmentHandler,
            score
        );
    }

    @Test
    void initializePreparesPlayerForWorld() {
        game.run();

        verify(player).addWeapon(any(Gun.class));
        verify(player).setActiveGun(0);
        verify(world).addEntity(player);
        verify(score).addPlayer(player);
    }

}