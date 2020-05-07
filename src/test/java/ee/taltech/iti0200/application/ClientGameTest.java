package ee.taltech.iti0200.application;

import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.handler.client.ClientGunShotHandler;
import ee.taltech.iti0200.domain.event.handler.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.handler.client.MatchRestartHandler;
import ee.taltech.iti0200.domain.event.handler.client.UpdateScoreHandler;
import ee.taltech.iti0200.domain.event.handler.common.ChangeEquipmentHandler;
import ee.taltech.iti0200.domain.event.handler.common.CollisionHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityCreateHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityGiveGunHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityHealingHandler;
import ee.taltech.iti0200.domain.event.handler.common.EntityRemoveHandler;
import ee.taltech.iti0200.domain.event.handler.common.MoveBodyHandler;
import ee.taltech.iti0200.domain.event.handler.client.PlayerRespawnHandler;
import ee.taltech.iti0200.graphics.GameGraphics;
import ee.taltech.iti0200.input.GameInput;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.physics.Physics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientGameTest {

    World world;
    EventBus eventBus;
    Timer timer;
    Player player;
    GameGraphics gameGraphics;
    Network network;
    GameInput input;
    Physics physics;
    EntityDamageHandler damageHandler;
    EntityHealingHandler healingHandler;
    EntityRemoveHandler entityRemoveHandler;
    EntityCreateHandler entityCreateHandler;
    EntityGiveGunHandler entityGiveGunHandler;
    MoveBodyHandler moveBodyHandler;
    CollisionHandler collisionHandler;
    PlayerRespawnHandler respawnHandler;
    UpdateScoreHandler scoreHandler;
    ClientGunShotHandler gunShotHandler;
    ChangeEquipmentHandler equipmentHandler;
    MatchRestartHandler restartHandler;
    Score score;

    private ClientGame game;

    @BeforeEach
    void setUp() {
        world = mock(World.class);
        eventBus = mock(EventBus.class);
        timer = mock(Timer.class);
        player = mock(Player.class);
        gameGraphics = mock(GameGraphics.class);
        network = mock(Network.class);
        input = mock(GameInput.class);
        physics = mock(Physics.class);
        damageHandler = mock(EntityDamageHandler.class);
        healingHandler = mock(EntityHealingHandler.class);
        entityRemoveHandler = mock(EntityRemoveHandler.class);
        entityCreateHandler = mock(EntityCreateHandler.class);
        entityGiveGunHandler = mock(EntityGiveGunHandler.class);
        moveBodyHandler = mock(MoveBodyHandler.class);
        collisionHandler = mock(CollisionHandler.class);
        respawnHandler = mock(PlayerRespawnHandler.class);
        scoreHandler = mock(UpdateScoreHandler.class);
        gunShotHandler = mock(ClientGunShotHandler.class);
        equipmentHandler = mock(ChangeEquipmentHandler.class);
        restartHandler = mock(MatchRestartHandler.class);
        score = mock(Score.class);

        game = new ClientGame(
            world,
            eventBus,
            timer,
            player,
            gameGraphics,
            network,
            input,
            physics,
            damageHandler,
            healingHandler,
            entityRemoveHandler,
            entityCreateHandler,
            entityGiveGunHandler,
            moveBodyHandler,
            collisionHandler,
            respawnHandler,
            scoreHandler,
            gunShotHandler,
            equipmentHandler,
            restartHandler,
            score
        );
    }

    @Test
    void constructorSubscribesHandlers() {
        verify(eventBus, times(12)).subscribe(any(), any());
    }

    @Test
    void initializePreparesPlayerForWorld() {
        game.run();

        verify(player).addEquipment(any(Gun.class));
        verify(player).setActiveGun(0);
        verify(world).addEntity(player);
        verify(score).addPlayer(player);
    }

    @Test
    void runAvoidsLoopIfInitializationThrowsException() throws Exception {
        doThrow(new RuntimeException()).when(timer).initialize();

        game.run();

        verify(network, never()).initialize();
        verify(gameGraphics, never()).isWindowOpen();
    }

    @Test
    void loopDelegatesToNetwork() {
        when(timer.sleep()).thenReturn(1L);
        when(gameGraphics.isWindowOpen()).thenReturn(true).thenReturn(true).thenReturn(false);

        game.run();

        verify(world).update(0L);
        verify(world).update(1L);
        verify(network).propagate(0L);
        verify(network).propagate(1L);
    }

    @Test
    void isGameRunningDelegatesToGraphics() {
        game.run();

        verify(gameGraphics).isWindowOpen();
    }

}
