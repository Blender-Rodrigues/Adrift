package ee.taltech.iti0200.domain.event.handler.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.GameWon;
import ee.taltech.iti0200.domain.event.UpdateScore;
import ee.taltech.iti0200.domain.event.handler.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.RespawnPlayer;
import ee.taltech.iti0200.physics.Vector;

import static ee.taltech.iti0200.network.message.Receiver.ALL_CLIENTS;
import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

public class ServerDamageHandler extends EntityDamageHandler {

    private static final int GAME_WON_SCORE = 20;

    @Inject
    public ServerDamageHandler(World world, Score score, EventBus eventBus) {
        super(world, score, eventBus);
    }

    @Override
    protected void fatal(Damageable target, DamageSource source) {
        if (target instanceof Player) {
            score.addDeath((Player) target);
        }
        if (source.getOwner() != null && source.getOwner() instanceof Player && target instanceof Living) {
            Player player = (Player) source.getOwner();
            score.addKill(player);
            if (score.getKills(player) >= GAME_WON_SCORE) {
                eventBus.dispatch(new GameWon(player, ALL_CLIENTS));
            }
        }

        if (target instanceof Living) {
            eventBus.dispatch(new DropLoot((Living) target, SERVER));
        }

        if (target instanceof Player && ((Player) target).getLives() > 1) {
            Player player = (Player) target;

            int lives = player.getLives() - 1;
            Vector position = world.nextSpawnPoint();

            player.setHealth(player.getMaxHealth());
            player.setLives(lives);
            player.setPosition(position);

            eventBus.dispatch(new RespawnPlayer(target.getId(), position, lives, ALL_CLIENTS));
        } else {
            eventBus.dispatch(new RemoveEntity(target, EVERYONE));
        }

        eventBus.dispatch(new UpdateScore(score.getPlayerScores(), ALL_CLIENTS));
    }

}
