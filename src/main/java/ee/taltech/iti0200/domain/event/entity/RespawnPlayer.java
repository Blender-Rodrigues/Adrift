package ee.taltech.iti0200.domain.event.entity;

import ee.taltech.iti0200.domain.entity.Player;
import ee.taltech.iti0200.domain.event.Event;
import ee.taltech.iti0200.network.message.Message;
import ee.taltech.iti0200.network.message.Receiver;
import ee.taltech.iti0200.physics.Vector;

import java.util.UUID;

import static java.lang.String.format;

public class RespawnPlayer extends Event implements Message {

    private final UUID id;
    private final Vector position;
    private final int lives;

    public RespawnPlayer(UUID id, Vector position, int lives, Receiver receiver) {
        super(receiver);
        this.id = id;
        this.position = position;
        this.lives = lives;
    }

    public UUID getId() {
        return id;
    }

    public Vector getPosition() {
        return position;
    }

    public int getLives() {
        return lives;
    }

    @Override
    public String toString() {
        return format("RespawnPlayer{%s with %d lives at %s}", id, lives, position);
    }

}
