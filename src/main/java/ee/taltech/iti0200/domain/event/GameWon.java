package ee.taltech.iti0200.domain.event;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;

import java.util.UUID;

import static java.lang.String.format;

public class GameWon extends Event implements Message {

    private static final long serialVersionUID = 1L;

    private final UUID id;

    public GameWon(Player player, Receiver receiver) {
        super(receiver);
        this.id = player.getId();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return format("GameWon{%s}", id);
    }

}
