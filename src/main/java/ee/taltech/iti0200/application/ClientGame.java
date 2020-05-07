package ee.taltech.iti0200.application;

import com.google.inject.Inject;
import ee.taltech.iti0200.di.annotations.LocalPlayer;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.entity.equipment.Gun;
import ee.taltech.iti0200.domain.entity.equipment.Jetpack;
import ee.taltech.iti0200.domain.entity.equipment.SpecialGun;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.GameWon;
import ee.taltech.iti0200.domain.event.UpdateScore;
import ee.taltech.iti0200.domain.event.entity.AddGun;
import ee.taltech.iti0200.domain.event.entity.ChangeEquipment;
import ee.taltech.iti0200.domain.event.entity.CreateEntity;
import ee.taltech.iti0200.domain.event.entity.DealDamage;
import ee.taltech.iti0200.domain.event.entity.EntityCollide;
import ee.taltech.iti0200.domain.event.entity.GunShot;
import ee.taltech.iti0200.domain.event.entity.Heal;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.RespawnPlayer;
import ee.taltech.iti0200.domain.event.entity.UpdateVector;
import ee.taltech.iti0200.domain.event.handler.client.ClientGunShotHandler;
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
import ee.taltech.iti0200.graphics.Graphics;
import ee.taltech.iti0200.input.Input;
import ee.taltech.iti0200.network.Network;
import ee.taltech.iti0200.physics.Physics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientGame extends Game {

    private final Logger logger = LogManager.getLogger(ClientGame.class);

    private final Player player;
    private final Graphics graphics;
    private final Network network;
    private final Score score;

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
        EntityHealingHandler healingHandler,
        EntityRemoveHandler entityRemoveHandler,
        EntityCreateHandler entityCreateHandler,
        EntityGiveGunHandler entityGiveGunHandler,
        MoveBodyHandler moveBodyHandler,
        CollisionHandler collisionHandler,
        PlayerRespawnHandler respawnHandler,
        UpdateScoreHandler scoreHandler,
        ClientGunShotHandler gunShotHandler,
        ChangeEquipmentHandler equipmentHandler,
        MatchRestartHandler restartHandler,
        Score score
    ) {
        super(world, eventBus, timer);
        this.player = player;
        this.graphics = graphics;
        this.network = network;
        this.score = score;

        components.add(physics);
        components.add(graphics);
        components.add(input);
        components.add(network);

        eventBus.subscribe(DealDamage.class, damageHandler);
        eventBus.subscribe(Heal.class, healingHandler);
        eventBus.subscribe(RemoveEntity.class, entityRemoveHandler);
        eventBus.subscribe(CreateEntity.class, entityCreateHandler);
        eventBus.subscribe(AddGun.class, entityGiveGunHandler);
        eventBus.subscribe(UpdateVector.class, moveBodyHandler);
        eventBus.subscribe(EntityCollide.class, collisionHandler);
        eventBus.subscribe(RespawnPlayer.class, respawnHandler);
        eventBus.subscribe(UpdateScore.class, scoreHandler);
        eventBus.subscribe(GunShot.class, gunShotHandler);
        eventBus.subscribe(ChangeEquipment.class, equipmentHandler);
        eventBus.subscribe(GameWon.class, restartHandler);
    }

    @Override
    protected void initialize() {
        player.addWeapon(new Gun(player.getBoundingBox()));
        player.addWeapon(new SpecialGun(player.getBoundingBox()));
        player.setActiveGun(0);
        player.addEquipment(new Jetpack(player.getBoundingBox()));
        world.addEntity(player);

        logger.info("Added {} to the world", player);

        score.addPlayer(player);
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
