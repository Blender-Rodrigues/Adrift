package ee.taltech.iti0200.domain.event.server;

import com.google.inject.Inject;
import ee.taltech.iti0200.domain.Score;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.DamageSource;
import ee.taltech.iti0200.domain.entity.Damageable;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.EventBus;
import ee.taltech.iti0200.domain.event.UpdateScore;
import ee.taltech.iti0200.domain.event.client.EntityDamageHandler;
import ee.taltech.iti0200.domain.event.entity.DropLoot;
import ee.taltech.iti0200.domain.event.entity.RemoveEntity;
import ee.taltech.iti0200.domain.event.entity.RespawnPlayer;

import static ee.taltech.iti0200.network.message.Receiver.ALL_CLIENTS;
import static ee.taltech.iti0200.network.message.Receiver.EVERYONE;
import static ee.taltech.iti0200.network.message.Receiver.SERVER;

public class ServerDamageHandler extends EntityDamageHandler {

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
            score.addKill((Player) source.getOwner());
        }

        if (target instanceof Player && ((Player) target).getLives() > 2) {
            eventBus.dispatch(new RespawnPlayer(
                target.getId(),
                world.nextPlayerSpawnPoint(),
                ((Player) target).getLives() - 1,
                EVERYONE
            ));
        } else {
            eventBus.dispatch(new RemoveEntity(target, EVERYONE));
        }

        if (target instanceof Living) {
            eventBus.dispatch(new DropLoot((Living) target, SERVER));
        }

        eventBus.dispatch(new UpdateScore(score.getPlayerScores(), ALL_CLIENTS));
    }

}
