package ee.taltech.iti0200.domain.event.handler.client;

import com.google.inject.Inject;
import ee.taltech.iti0200.application.RestartGame;
import ee.taltech.iti0200.domain.World;
import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.GameWon;
import ee.taltech.iti0200.domain.event.Subscriber;

import java.util.UUID;

public class MatchRestartHandler implements Subscriber<GameWon> {

    private final World world;

    @Inject
    public MatchRestartHandler(World world) {
        this.world = world;
    }

    @Override
    public void handle(GameWon event) {
        UUID id = event.getId();
        Player player = (Player) world.getEntity(id);

        throw new RestartGame(String.format("Game won by %s", player == null ? id : player.getName()));
    }

}
